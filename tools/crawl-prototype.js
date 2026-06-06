const fs = require('fs');
const path = require('path');

const globalRoot = process.env.NPM_GLOBAL_ROOT || 'D:\\npm-global\\node_modules';
const { chromium } = require(path.join(globalRoot, '@playwright', 'mcp', 'node_modules', 'playwright'));

const startUrl = 'http://shengtaiprd.pancosky.com/5d/';
const workspace = process.cwd();
const screenshotDir = path.join(workspace, 'output', 'prototype-screenshots');
const reportFile = path.join(workspace, 'output', 'prototype-crawl.json');
const executablePath = process.argv[2] || 'C:\\Users\\18252\\AppData\\Local\\ms-playwright\\chromium-1223\\chrome-win64\\chrome.exe';

fs.mkdirSync(screenshotDir, { recursive: true });

const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
const clean = value => (value || '').replace(/\s+/g, ' ').replace(/\u00a0/g, ' ').trim();
const uniq = items => [...new Set(items.filter(Boolean))];
const slug = value => clean(value).replace(/[\\/:*?"<>|]+/g, '-').replace(/\s+/g, '-').slice(0, 80) || 'page';

const pageHints = [
  ['发布车源', ['车辆图片', '车架号', '保存草稿']],
  ['我的订单', ['卖出的车', '买到的车', '成交价']],
  ['消息中心', ['消息订阅', '订单状态更新', '新的订单合同']],
  ['AI助理', ['小慧助理', '历史任务', '按住说话']],
  ['车源详情', ['车源详情', '已收藏', '金融优惠']],
  ['平台客服', ['平台专属客服', '常见热点问题']],
  ['我的主页', ['我的主页', '我的优惠券', '金融服务']],
  ['车商认证', ['车商资质认证', '提交资质认证']],
  ['我的车行', ['我的车行', '认证车商', '在售车源']],
  ['金融服务', ['金融优惠方案', '金融额度中心', '申请金融额度']],
  ['收藏车源', ['收藏车源', '已筛选']],
  ['首页找车', ['5D好车', 'AI赋能二手车拓展商机', '最新']]
];

const clickLabels = [
  '找车', '交易', 'AI助理', '消息', '我的',
  '发布车源', '在线客服', '+ 开始',
  '最新', '新能源', '保证金', '出口', '全国', '搜索',
  '大众 Polo 2023款', '奔驰 CLE (进口)', '宝马 X5 2023款', '奥迪 A6L',
  '详情', '联系买家', '卖出的车', '买到的车', '全部', '待确认', '交易中', '争议中', '已完成', '已终止',
  '查看示例', '*品牌车型 请选择', '*上牌时间 请选择', '一口价', '拍卖', '*车身颜色 请选择', '*年检到期 请选择', '*强险到期 请选择',
  '保存草稿', '+AI帮我填', '发布', '行驶证', '产权证', '车身铭牌',
  '新的对话', '历史任务', '查一下奥迪Q5最近的价格走势', '帮我全网搜一下价格低于12万的宝马3系', '帮我自动上传新车源', '今日抢手车源简报分析',
  '自动推广', '订单状态更新', '新的订单合同', '可用保证金不足', '新成员申请加入您的车行',
  '登录 / 注册', '立即认证', '我的车行', '我的关注', '我的订单', '金融服务', '客服支持', '我的优惠券'
];

function inferPageName(text) {
  for (const [name, hints] of pageHints) {
    if (hints.every(hint => text.includes(hint)) || hints.some(hint => text.includes(hint))) {
      return name;
    }
  }
  return clean(text).slice(0, 24) || '未知页面';
}

async function extract(page, action, index) {
  await sleep(700);
  const data = await page.evaluate(() => {
    const textOf = el => (el.innerText || el.textContent || '').replace(/\s+/g, ' ').trim();
    const visible = el => {
      const rect = el.getBoundingClientRect();
      const style = window.getComputedStyle(el);
      return rect.width > 0 && rect.height > 0 && style.display !== 'none' && style.visibility !== 'hidden';
    };
    const all = [...document.querySelectorAll('body *')].filter(visible);
    const clickable = all
      .filter(el => ['BUTTON', 'A', 'INPUT', 'TEXTAREA', 'SELECT'].includes(el.tagName) || el.getAttribute('role') === 'button' || el.tabIndex >= 0 || getComputedStyle(el).cursor === 'pointer')
      .map(el => textOf(el) || el.getAttribute('placeholder') || el.getAttribute('aria-label') || '')
      .filter(Boolean);
    const inputs = [...document.querySelectorAll('input, textarea, select')]
      .filter(visible)
      .map(el => ({
        tag: el.tagName.toLowerCase(),
        type: el.getAttribute('type') || '',
        placeholder: el.getAttribute('placeholder') || '',
        value: el.value || '',
        label: el.getAttribute('aria-label') || ''
      }));
    const tabWords = ['全部', '待确认', '交易中', '争议中', '已完成', '已终止', '消息', '订阅', '一口价', '拍卖', '卖出的车', '买到的车', '最新', '新能源', '保证金', '出口'];
    return {
      title: document.title,
      url: location.href,
      text: textOf(document.body),
      buttons: clickable,
      inputs,
      tabs: tabWords.filter(word => textOf(document.body).includes(word)),
      tables: [...document.querySelectorAll('table')].map(table => ({
        headers: [...table.querySelectorAll('th')].map(textOf).filter(Boolean),
        cells: [...table.querySelectorAll('td')].slice(0, 50).map(textOf).filter(Boolean)
      })),
      dialogs: all
        .filter(el => el.getAttribute('role') === 'dialog' || /modal|popup|dialog|drawer|sheet/i.test(el.className || ''))
        .map(textOf)
        .filter(Boolean)
    };
  });

  data.action = action;
  data.text = clean(data.text);
  data.pageName = inferPageName(data.text);
  data.buttons = uniq(data.buttons.map(clean)).filter(v => v.length <= 80).slice(0, 120);
  data.dialogs = uniq(data.dialogs.map(clean)).slice(0, 20);
  data.screenshot = path.join('output', 'prototype-screenshots', `${String(index).padStart(2, '0')}-${slug(data.pageName)}.png`).replace(/\\/g, '/');
  await page.screenshot({ path: path.join(workspace, data.screenshot), fullPage: true });
  return data;
}

async function clickSmallestText(page, label) {
  const match = await page.evaluate(target => {
    const normalize = value => (value || '').replace(/\s+/g, ' ').trim();
    const visible = el => {
      const rect = el.getBoundingClientRect();
      const style = window.getComputedStyle(el);
      return rect.width > 0 && rect.height > 0 && style.display !== 'none' && style.visibility !== 'hidden';
    };
    const candidates = [...document.querySelectorAll('button,a,[role=button],[tabindex],input,textarea,select,div,span')]
      .filter(visible)
      .map((el, idx) => {
        const text = normalize(el.innerText || el.textContent || el.getAttribute('placeholder') || el.getAttribute('aria-label') || '');
        const rect = el.getBoundingClientRect();
        const clickable = ['BUTTON', 'A', 'INPUT', 'TEXTAREA', 'SELECT'].includes(el.tagName) || el.getAttribute('role') === 'button' || el.tabIndex >= 0 || getComputedStyle(el).cursor === 'pointer';
        return { idx, text, clickable, area: rect.width * rect.height, x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };
      })
      .filter(item => item.text && (item.text === target || item.text.includes(target) || target.includes(item.text)))
      .filter(item => item.clickable || item.text.length <= target.length + 20)
      .sort((a, b) => a.area - b.area || a.text.length - b.text.length);
    return candidates[0] || null;
  }, label);

  if (!match) return false;
  await page.mouse.click(match.x, match.y);
  return true;
}

async function main() {
  const browser = await chromium.launch({ headless: true, executablePath });
  const page = await browser.newPage({ viewport: { width: 390, height: 844 }, isMobile: true });
  const snapshots = [];
  const edges = [];
  let shotIndex = 1;

  async function openStart() {
    for (let attempt = 0; attempt < 3; attempt++) {
      try {
        await page.goto(startUrl, { waitUntil: 'domcontentloaded', timeout: 60000 });
        await page.waitForSelector('body', { timeout: 10000 });
        await sleep(1500);
        return;
      } catch (error) {
        if (attempt === 2) throw error;
        await sleep(1200);
      }
    }
  }

  await openStart();
  snapshots.push(await extract(page, '初始打开', shotIndex++));

  for (const label of clickLabels) {
    await openStart().catch(() => {});
    await sleep(500);
    const before = inferPageName(clean(await page.locator('body').innerText().catch(() => '')));
    const ok = await clickSmallestText(page, label).catch(() => false);
    if (!ok) continue;
    const snap = await extract(page, label, shotIndex++);
    snapshots.push(snap);
    edges.push({ from: before, action: label, to: snap.pageName });

    for (const child of ['详情', '联系买家', '聊天', '在线付定', '查看示例', '保存草稿', '+AI帮我填', '发布']) {
      if (!snap.text.includes(child)) continue;
      const childBefore = snap.pageName;
      const childOk = await clickSmallestText(page, child).catch(() => false);
      if (!childOk) continue;
      const childSnap = await extract(page, `${label} > ${child}`, shotIndex++);
      snapshots.push(childSnap);
      edges.push({ from: childBefore, action: child, to: childSnap.pageName });
      break;
    }
  }

  await browser.close();
  fs.writeFileSync(reportFile, JSON.stringify({ startUrl, snapshots, edges }, null, 2), 'utf8');
  console.log(`screenshots=${snapshots.length}`);
  console.log(`edges=${edges.length}`);
  console.log(reportFile);
}

main().catch(error => {
  console.error(error);
  process.exit(1);
});
