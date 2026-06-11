const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const OUTPUT_DIR = path.join(__dirname, 'output', 'screenshots');
const URL = 'http://shengtaiprd.pancosky.com/5d/';

(async () => {
  if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });

  const browser = await puppeteer.launch({
    executablePath: 'C:/Program Files/Google/Chrome/Application/chrome.exe',
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--window-size=375,812']
  });

  const page = await browser.newPage();
  await page.setViewport({ width: 375, height: 812 });

  await page.goto(URL, { waitUntil: 'networkidle2', timeout: 30000 });
  await new Promise(r => setTimeout(r, 3000));

  async function clickText(text, waitMs = 2000) {
    return page.evaluate((t) => {
      const els = document.querySelectorAll('*');
      for (const el of els) {
        const content = el.textContent.trim();
        if ((content === t || content.startsWith(t)) && el.children.length <= 3 && el.offsetHeight > 0) {
          el.click();
          return true;
        }
      }
      return false;
    }, text).then(async (clicked) => {
      if (clicked) await new Promise(r => setTimeout(r, waitMs));
      return clicked;
    });
  }

  async function screenshot(name) {
    await page.screenshot({ path: path.join(OUTPUT_DIR, name), fullPage: false });
    console.log('Screenshot:', name);
  }

  async function getVisibleTexts() {
    return page.evaluate(() => {
      const texts = [];
      document.querySelectorAll('*').forEach(el => {
        if (el.children.length === 0 && el.offsetHeight > 0) {
          const t = el.textContent.trim();
          if (t && t.length >= 2 && t.length <= 30) texts.push(t);
        }
      });
      return [...new Set(texts)];
    });
  }

  // 1. Home page
  await screenshot('01-home.png');
  const homeTexts = await getVisibleTexts();
  fs.writeFileSync(path.join(OUTPUT_DIR, '01-home-text.txt'), homeTexts.join('\n'), 'utf8');

  // 2. Click each bottom tab
  const tabs = ['找车', '交易', '求购', 'AI助理', '消息', '我的'];
  for (let i = 0; i < tabs.length; i++) {
    await clickText(tabs[i]);
    await screenshot(`0${i+2}-tab-${tabs[i]}.png`);
    const texts = await getVisibleTexts();
    fs.writeFileSync(path.join(OUTPUT_DIR, `0${i+2}-tab-${tabs[i]}-text.txt`), texts.join('\n'), 'utf8');
  }

  // 3. On "我的" page, click each feature
  await clickText('我的');
  const profileFeatures = [
    '我的车源', 'AI分发车源', 'AI行情简报', '收藏车源', '我的车行',
    '浏览记录', '我的交易', '金融服务', '电子合同', '我的关注',
    '交易规范', '客服帮助', '系统设置', '发布车源', '发布求购'
  ];

  for (let i = 0; i < profileFeatures.length; i++) {
    const f = profileFeatures[i];
    // Go back to profile first
    await page.goto(URL, { waitUntil: 'networkidle2', timeout: 15000 });
    await new Promise(r => setTimeout(r, 2000));
    await clickText('我的');
    await new Promise(r => setTimeout(r, 1000));

    const clicked = await clickText(f);
    if (clicked) {
      await screenshot(`profile-${f}.png`);
      const texts = await getVisibleTexts();
      fs.writeFileSync(path.join(OUTPUT_DIR, `profile-${f}-text.txt`), texts.join('\n'), 'utf8');
    } else {
      console.log(`Could not click: ${f}`);
    }
  }

  // 4. Try clicking a car card on home
  await page.goto(URL, { waitUntil: 'networkidle2', timeout: 15000 });
  await new Promise(r => setTimeout(r, 2000));

  // Click first car card
  const carClicked = await page.evaluate(() => {
    const cards = document.querySelectorAll('[class*="card"], [class*="car"]');
    for (const card of cards) {
      if (card.offsetHeight > 100 && card.textContent.includes('万')) {
        card.click();
        return true;
      }
    }
    return false;
  });
  if (carClicked) {
    await new Promise(r => setTimeout(r, 2000));
    await screenshot('car-detail.png');
    const texts = await getVisibleTexts();
    fs.writeFileSync(path.join(OUTPUT_DIR, 'car-detail-text.txt'), texts.join('\n'), 'utf8');
  }

  await browser.close();
  console.log('\nAll done! Screenshots:', OUTPUT_DIR);
})().catch(e => {
  console.error('Error:', e.message);
  process.exit(1);
});
