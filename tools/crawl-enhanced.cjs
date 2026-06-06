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

  // Load existing data
  const existingFile = path.join(process.cwd(), 'output', 'prototype-analysis.json');
  let existing = { pages: [], edges: [] };
  try { existing = JSON.parse(fs.readFileSync(existingFile, 'utf8')); } catch(e) {}
  allResults.push(...existing.pages);
  edges.push(...existing.edges);
  shotIdx = allResults.length + 1;

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

  async function goHome() {
    // Use bottom nav to go back to home
    await page.evaluate(() => {
      const txt = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
      const vis = el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width>0 && r.height>0 && s.display!=='none' && s.visibility!=='hidden'; };
      const nav = [...document.querySelectorAll('button,a,[role=button],[tabindex],div,span')].filter(vis)
        .filter(el => { const t = txt(el); return t === '找车'; })
        .map(el => { const r = el.getBoundingClientRect(); return { x: r.left+r.width/2, y: r.top+r.height/2, area: r.width*r.height }; })
        .sort((a,b) => a.area - b.area);
      if (nav[0]) { return { x: nav[0].x, y: nav[0].y }; }
      return null;
    }).then(async hit => {
      if (hit) { await page.mouse.click(hit.x, hit.y); await sleep(1000); }
    });
  }

  async function capture(name, from, action) {
    // Skip if already captured
    if (allResults.find(r => r.name === name)) { console.log(`[SKIP] ${name} already captured`); return null; }
    const d = await extract(page, name);
    d.screenshot = await takeShot(page, name);
    allResults.push(d);
    if (from) edges.push({ from, action: action || name, to: name });
    console.log(`[${allResults.length}] ${name}: ${d.buttons.length} btns`);
    return d;
  }

  // ═══════════════════════════════════════
  // Navigate and capture all pages
  // ═══════════════════════════════════════
  await page.goto('http://shengtaiprd.pancosky.com/5d/', { waitUntil: 'networkidle', timeout: 60000 });
  await page.waitForTimeout(3000);

  // ── 交易 sub-tabs ──
  await clickLabel(page, '交易'); await sleep(800);
  for (const tab of ['卖出的车', '买到的车', '全部', '待确认', '交易中', '争议中', '已完成', '已终止']) {
    const h = await clickLabel(page, tab);
    if (h) await capture('交易-' + tab, '交易', '切换' + tab);
  }

  // Click 详情 on an order
  const detailH = await clickLabel(page, '详情');
  if (detailH) await capture('订单详情', '交易', '点击详情');
  await page.goBack().catch(()=>{}); await sleep(800);

  // ── AI助理 ──
  await clickLabel(page, 'AI助理'); await sleep(1500);
  await capture('AI助理-主页', '首页-找车', '点击AI助理');

  // AI sub items
  const aiItems = ['新的对话', '历史任务'];
  for (const item of aiItems) {
    await clickLabel(page, 'AI助理'); await sleep(800);
    const h = await clickLabel(page, item);
    if (h) { await capture('AI-' + item, 'AI助理-主页', item); await page.keyboard.press('Escape').catch(()=>{}); await sleep(500); }
  }

  // AI suggested queries
  const aiQueries = ['查一下奥迪Q5最近的价格走势', '帮我全网搜一下价格低于12万的宝马3系', '帮我自动上传新车源', '今日抢手车源简报分析'];
  for (const q of aiQueries) {
    await clickLabel(page, 'AI助理'); await sleep(800);
    const h = await clickLabel(page, q);
    if (h) { await capture('AI-Query-' + q.slice(0,8), 'AI助理-主页', q); await page.goBack().catch(()=>{}); await sleep(500); }
  }

  // ── 消息 ──
  await clickLabel(page, '消息'); await sleep(800);
  await capture('消息-主页', '首页-找车', '点击消息');

  // 消息 tab: 订阅
  const subH = await clickLabel(page, '订阅');
  if (subH) await capture('消息-订阅', '消息-主页', '切换订阅');

  // Click message items
  const msgItems = ['自动推广', '订单状态更新', '新的订单合同', '可用保证金不足', '新成员申请加入您的车行', '张学友'];
  for (const m of msgItems) {
    await clickLabel(page, '消息'); await sleep(800);
    await clickLabel(page, '消息'); await sleep(500); // ensure on message tab
    const h = await clickLabel(page, m);
    if (h) { await capture('消息-' + m.slice(0,8), '消息-主页', '点击' + m); await page.goBack().catch(()=>{}); await sleep(800); }
  }

  // ── 我的 ──
  await clickLabel(page, '我的'); await sleep(800);
  await capture('我的-主页', '首页-找车', '点击我的');

  const myItems = [
    '我的车源', 'AI分发车源', 'AI行情简报',
    '收藏车源', '我的车行', '浏览记录', '我的订单',
    '金融服务', '电子合同', '我的关注', '我的优惠券',
    '交易规范', '使用教程', '客服支持', '系统设置'
  ];
  for (const m of myItems) {
    await clickLabel(page, '我的'); await sleep(800);
    const h = await clickLabel(page, m);
    if (h) {
      await capture('我的-' + m, '我的-主页', '点击' + m);
      // Try to go back
      await page.goBack().catch(()=>{}); await sleep(800);
    }
  }

  // ── 首页 sub ──
  await goHome();

  // 发布车源
  const pubH = await clickLabel(page, '发布车源');
  if (pubH) { await capture('发布车源', '首页-找车', '点击发布车源'); await page.goBack().catch(()=>{}); await sleep(1000); }

  // 在线客服
  await goHome();
  const csH = await clickLabel(page, '在线客服');
  if (csH) { await capture('在线客服', '首页-找车', '点击在线客服'); await page.goBack().catch(()=>{}); await sleep(1000); }

  // 车源详情 (click first car)
  await goHome();
  const carHit = await page.evaluate(() => {
    const txt = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
    const vis = el => { const r = el.getBoundingClientRect(), s = getComputedStyle(el); return r.width>0 && r.height>0 && s.display!=='none' && s.visibility!=='hidden'; };
    const cards = [...document.querySelectorAll('div,li,section,article')].filter(vis)
      .filter(el => { const t = txt(el); return t.includes('万') && t.includes('公里') && el.children.length >= 2 && el.getBoundingClientRect().width > 100; })
      .map(el => { const r = el.getBoundingClientRect(); return { x: r.left+r.width/2, y: r.top+r.height/2, area: r.width*r.height }; })
      .sort((a,b) => a.area - b.area);
    return cards[0] || null;
  });
  if (carHit) {
    await page.mouse.click(carHit.x, carHit.y); await sleep(1500);
    await capture('车源详情-v2', '首页-找车', '点击车源卡片');

    // Try sub actions on detail page
    const shareH = await clickLabel(page, '分享');
    if (shareH) { await capture('分享弹窗', '车源详情-v2', '点击分享'); await page.keyboard.press('Escape').catch(()=>{}); await sleep(500); }

    const followH = await clickLabel(page, '+ 关注');
    if (followH) { await capture('关注后', '车源详情-v2', '点击关注'); }

    const reportH = await clickLabel(page, '查看');
    if (reportH) { await capture('检测报告', '车源详情-v2', '点击查看检测报告'); await page.goBack().catch(()=>{}); await sleep(500); }

    await page.goBack().catch(()=>{}); await sleep(1000);
  }

  // ═══ Save final report ═══
  await browser.close();
  const report = { url: 'http://shengtaiprd.pancosky.com/5d/', crawledAt: new Date().toISOString(), pages: allResults, edges };
  fs.writeFileSync(existingFile, JSON.stringify(report, null, 2), 'utf8');
  console.log(`\nDONE: ${allResults.length} pages, ${edges.length} edges`);
})();
