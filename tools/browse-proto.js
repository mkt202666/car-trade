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

  console.log('Navigating to:', URL);
  await page.goto(URL, { waitUntil: 'networkidle2', timeout: 30000 });
  await new Promise(r => setTimeout(r, 3000));

  // Take screenshot of home page
  await page.screenshot({ path: path.join(OUTPUT_DIR, '01-home.png'), fullPage: false });
  console.log('Screenshot: 01-home.png');

  // Try to find and click each tab/navigation item
  const navItems = await page.evaluate(() => {
    const items = [];
    // Look for tab bar items
    document.querySelectorAll('[class*="tab"], [class*="nav"], [class*="menu"], [role="tab"]').forEach(el => {
      const text = el.textContent.trim();
      if (text && text.length < 10) items.push({ text, tag: el.tagName });
    });
    return items;
  });
  console.log('Nav items found:', JSON.stringify(navItems));

  // Get all clickable elements with Chinese text
  const clickableItems = await page.evaluate(() => {
    const items = [];
    document.querySelectorAll('a, button, [role="button"], [class*="item"], [class*="card"], [class*="menu"]').forEach(el => {
      const text = el.textContent.trim().substring(0, 20);
      if (text && text.length >= 2 && text.length <= 20) {
        items.push({ text, tag: el.tagName, className: el.className?.substring(0, 50) || '' });
      }
    });
    return [...new Set(items.map(i => JSON.stringify(i)))].map(s => JSON.parse(s)).slice(0, 50);
  });
  console.log('Clickable items:', JSON.stringify(clickableItems, null, 2));

  // Try clicking bottom tabs to see different pages
  const tabs = ['找车', '求购', 'AI助理', '消息', '我的'];
  for (let i = 0; i < tabs.length; i++) {
    try {
      const tab = tabs[i];
      const clicked = await page.evaluate((text) => {
        const els = document.querySelectorAll('*');
        for (const el of els) {
          if (el.textContent.trim() === text && el.children.length <= 2) {
            el.click();
            return true;
          }
        }
        return false;
      }, tab);
      if (clicked) {
        await new Promise(r => setTimeout(r, 2000));
        await page.screenshot({ path: path.join(OUTPUT_DIR, `0${i+2}-tab-${tab}.png`), fullPage: false });
        console.log(`Screenshot: 0${i+2}-tab-${tab}.png`);
      }
    } catch (e) {
      console.log(`Tab ${tabs[i]} error:`, e.message);
    }
  }

  // Get the full page text content for analysis
  const pageText = await page.evaluate(() => document.body.innerText);
  fs.writeFileSync(path.join(OUTPUT_DIR, 'page-content.txt'), pageText, 'utf8');
  console.log('Page content saved to page-content.txt');

  await browser.close();
  console.log('Done!');
})().catch(e => {
  console.error('Error:', e.message);
  process.exit(1);
});
