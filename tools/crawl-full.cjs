const { chromium } = require('D:/npm-global/node_modules/@playwright/mcp/node_modules/playwright');
const fs = require('fs');
const path = require('path');

const screenshotDir = path.join(process.cwd(), 'output', 'prototype-screenshots');
fs.mkdirSync(screenshotDir, { recursive: true });

const execPath = 'C:\\Users\\18252\\AppData\\Local\\ms-playwright\\chromium-1223\\chrome-win64\\chrome.exe';
const sleep = ms => new Promise(r => setTimeout(r, ms));

(async () => {
  const browser = await chromium.launch({ headless: true, executablePath: execPath });
  const ctx = await browser.newContext({ viewport: { width: 390, height: 844 }, isMobile: true });
  const page = await ctx.newPage();

  // Proxy via Node fetch to bypass server 502
  await page.route('**/*', async (route) => {
    try {
      const url = route.request().url();
      const r = await fetch(url);
      const buf = await r.arrayBuffer();
      const hdr = {};
      r.headers.forEach((v, k) => { hdr[k] = v; });
      await route.fulfill({ status: r.status, headers: hdr, body: Buffer.from(buf) });
    } catch (e) { await route.continue(); }
  });

  const allResults = [];
  const edges = [];
  let shotIdx = 1;

  async function clickLabel(pg, label) {
    const found = await pg.evaluate(target => {
      const txt = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
      const vis = el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width>0 && r.height>0 && s.display!=='none' && s.visibility!=='hidden'; };
      const cands = [...document.querySelectorAll('button,a,[role=button],[tabindex],input,textarea,select,div,span')]
        .filter(vis).map(el => {
          const t = txt(el) || el.getAttribute('placeholder') || el.getAttribute('aria-label') || '';
          const r = el.getBoundingClientRect();
          const ck = ['BUTTON','A','INPUT','TEXTAREA','SELECT'].includes(el.tagName) || el.getAttribute('role')==='button' || el.tabIndex>=0 || getComputedStyle(el).cursor==='pointer';
          return { t: t.trim(), ck, area: r.width*r.height, x: r.left+r.width/2, y: r.top+r.height/2 };
        })
        .filter(i => i.t && (i.t===target || i.t.includes(target) || target.includes(i.t)))
        .filter(i => i.ck || i.t.length <= target.length+20)
        .sort((a,b) => a.area - b.area || a.t.length - b.t.length);
      return cands[0] ? { x: cands[0].x, y: cands[0].y } : null;
    }, label);
    if (found) { await pg.mouse.click(found.x, found.y); await sleep(1200); }
    return found;
  }

  async function extract(pg, name) {
    await sleep(600);
    return await pg.evaluate(nm => {
      const txt = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
      const vis = el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width>0 && r.height>0 && s.display!=='none' && s.visibility!=='hidden'; };
      const all = [...document.querySelectorAll('body *')].filter(vis);
      const buttons = [...new Set(all.filter(el => ['BUTTON','A','INPUT','TEXTAREA','SELECT'].includes(el.tagName)||el.getAttribute('role')==='button'||el.tabIndex>=0||getComputedStyle(el).cursor==='pointer')
        .map(el => txt(el)||el.getAttribute('placeholder')||el.getAttribute('aria-label')||'').filter(t=>t&&t.length<=80))].slice(0,100);
      const inputs = [...document.querySelectorAll('input,textarea,select')].filter(vis).map(el=>({tag:el.tagName.toLowerCase(),type:el.getAttribute('type')||'',placeholder:el.getAttribute('placeholder')||'',label:el.getAttribute('aria-label')||''}));
      const btxt = txt(document.body);
      const tabW = ['全部','待确认','交易中','争议中','已完成','已终止','消息','订阅','一口价','拍卖','卖出的车','买到的车','最新','新能源','保证金','出口','待审核','已上架','已下架','草稿','在售','已锁车','已成交','收藏车源','我的车源'];
      const tabs = tabW.filter(w => btxt.includes(w));
      const tables = [...document.querySelectorAll('table')].map(t=>({headers:[...t.querySelectorAll('th')].map(txt).filter(Boolean),rows:[...t.querySelectorAll('tr')].slice(0,10).map(r=>[...r.querySelectorAll('td')].map(txt).filter(Boolean))}));
      const dialogs = [...new Set(all.filter(el=>el.getAttribute('role')==='dialog'||/modal|popup|dialog|drawer|sheet/i.test(el.className||'')).map(txt).filter(t=>t&&t.length>2))].slice(0,10);
      return { name:nm, title:document.title, url:location.href, bodyText:btxt.slice(0,3000), buttons, inputs, tabs, tables, dialogs };
    }, name);
  }

  async function takeShot(pg, name) {
    const fname = String(shotIdx).padStart(2,'0') + '-' + name.replace(/[\\/:*?"<>|\s]+/g, '-').slice(0,40) + '.png';
    await pg.screenshot({ path: path.join(screenshotDir, fname), fullPage: true });
    shotIdx++;
    return 'output/prototype-screenshots/' + fname;
  }

  async function goHome() { await clickLabel(page, '找车'); await sleep(800); }

  async function capture(name, from, action) {
    const d = await extract(page, name);
    d.screenshot = await takeShot(page, name);
    allResults.push(d);
    if (from) edges.push({ from, action: action || name, to: name });
    console.log(`[${allResults.length}] ${name}: ${d.buttons.length} btns`);
    return d;
  }

  // ═══ Phase 1: Main tabs ═══
  await page.goto('http://shengtaiprd.pancosky.com/5d/', { waitUntil: 'networkidle', timeout: 60000 });
  await page.waitForTimeout(3000);
  await capture('首页-找车', null, null);
  await clickLabel(page, '交易'); await capture('交易', '首页-找车', '点击交易');
  await clickLabel(page, 'AI助理'); await capture('AI助理', '交易', '点击AI助理');
  await clickLabel(page, '消息'); await capture('消息', 'AI助理', '点击消息');
  await clickLabel(page, '我的'); await capture('我的', '消息', '点击我的');

  // ═══ Phase 2: 首页子页面 ═══
  await goHome();
  const carHit = await page.evaluate(() => {
    const txt = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
    const vis = el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width>0 && r.height>0 && s.display!=='none' && s.visibility!=='hidden'; };
    const cards = [...document.querySelectorAll('div,li,section,article')].filter(vis)
      .filter(el => { const t = txt(el); return t.includes('万') && t.includes('公里') && el.children.length >= 2; })
      .map(el => { const r = el.getBoundingClientRect(); return { x: r.left+r.width/2, y: r.top+r.height/2, area: r.width*r.height }; })
      .sort((a,b) => a.area - b.area);
    return cards[0] || null;
  });
  if (carHit) {
    await page.mouse.click(carHit.x, carHit.y); await sleep(1500);
    await capture('车源详情', '首页-找车', '点击车源卡片');
    await page.goBack().catch(()=>{}); await sleep(1500);
  }

  await goHome();
  const pubH = await clickLabel(page, '发布车源');
  if (pubH) { await capture('发布车源', '首页-找车', '点击发布车源'); await page.goBack().catch(()=>{}); await sleep(1000); }

  await goHome();
  const csH = await clickLabel(page, '在线客服');
  if (csH) { await capture('在线客服', '首页-找车', '点击在线客服'); await page.goBack().catch(()=>{}); await sleep(1000); }

  await goHome();
  for (const f of ['最新', '新能源', '保证金', '出口']) {
    const h = await clickLabel(page, f);
    if (h) await capture('筛选-' + f, '首页-找车', '筛选' + f);
  }

  // ═══ Phase 3: 交易子页面 ═══
  await clickLabel(page, '交易'); await sleep(800);
  for (const t of ['卖出的车', '买到的车', '待确认', '交易中', '争议中', '已完成', '已终止']) {
    const h = await clickLabel(page, t);
    if (h) await capture('交易-' + t, '交易', '切换' + t);
  }

  // ═══ Phase 4: AI助理子页面 ═══
  await clickLabel(page, 'AI助理'); await sleep(800);
  for (const b of ['新的对话', '历史任务', '查一下奥迪Q5最近的价格走势', '帮我全网搜一下价格低于12万的宝马3系', '帮我自动上传新车源', '今日抢手车源简报分析']) {
    const h = await clickLabel(page, b);
    if (h) { await capture('AI-' + b.slice(0,15), 'AI助理', b); await page.keyboard.press('Escape').catch(()=>{}); await sleep(500); }
  }

  // ═══ Phase 5: 消息子页面 ═══
  await clickLabel(page, '消息'); await sleep(800);
  for (const m of ['自动推广', '订单状态更新', '新的订单合同', '可用保证金不足', '新成员申请加入您的车行']) {
    const h = await clickLabel(page, m);
    if (h) { await capture('消息-' + m.slice(0,10), '消息', m); await page.goBack().catch(()=>{}); await sleep(800); await clickLabel(page, '消息'); await sleep(800); }
  }

  // ═══ Phase 6: 我的子页面 ═══
  await clickLabel(page, '我的'); await sleep(800);
  for (const m of ['我的订单', '我的车行', '金融服务', '我的优惠券', '客服支持']) {
    const h = await clickLabel(page, m);
    if (h) { await capture('我的-' + m, '我的', m); await page.goBack().catch(()=>{}); await sleep(800); await clickLabel(page, '我的'); await sleep(800); }
  }

  const loginH = await clickLabel(page, '登录 / 注册');
  if (loginH) { await capture('登录注册', '我的', '点击登录注册'); await page.goBack().catch(()=>{}); await sleep(800); await clickLabel(page, '我的'); await sleep(800); }

  const certH = await clickLabel(page, '立即认证');
  if (certH) { await capture('车商认证', '我的', '点击立即认证'); await page.goBack().catch(()=>{}); await sleep(800); }

  // ═══ Save ═══
  await browser.close();
  const report = { url: 'http://shengtaiprd.pancosky.com/5d/', pages: allResults, edges };
  fs.writeFileSync(path.join(process.cwd(), 'output', 'prototype-analysis.json'), JSON.stringify(report, null, 2), 'utf8');
  console.log(`\nDONE: ${allResults.length} pages, ${edges.length} edges`);
})();
