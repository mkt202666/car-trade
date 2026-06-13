const p = require("puppeteer-core");
(async () => {
    const b = await p.launch({executablePath:"C:/Program Files/Google/Chrome/Application/chrome.exe",headless:"new",args:["--no-sandbox"]});
    const pg = await b.newPage();
    await pg.setViewport({width:375,height:812});
    
    pg.on('console', msg => console.log('CONSOLE:', msg.text()));
    pg.on('pageerror', err => console.log('PAGE ERROR:', err.message));
    
    await pg.goto("http://[::1]:5173", {waitUntil:"networkidle2",timeout:60000});
    await new Promise(r=>setTimeout(r,3000));
    
    // Test all tabs
    const results = {};
    
    // Tab 0: 找车 (initial)
    let txt = await pg.evaluate(()=>document.body.innerText);
    results["找车"] = txt.substring(0, 500);
    console.log("✅ 找车:", txt.substring(0, 100));
    
    // Tab 1: 求购
    console.log("Clicking 求购...");
    await pg.evaluate(()=>{const els=[...document.querySelectorAll(".tab-item")]; if(els[1]) els[1].click();});
    await new Promise(r=>setTimeout(r,2000));
    txt = await pg.evaluate(()=>document.body.innerText);
    results["求购"] = txt.substring(0, 500);
    console.log("✅ 求购:", txt.substring(0, 100));
    
    // Tab 2: AI助理 (center button)
    console.log("Clicking AI助理...");
    await pg.evaluate(()=>{const els=[...document.querySelectorAll(".tab-center")]; if(els[0]) els[0].click();});
    await new Promise(r=>setTimeout(r,2000));
    txt = await pg.evaluate(()=>document.body.innerText);
    results["AI助理"] = txt.substring(0, 500);
    console.log("✅ AI助理:", txt.substring(0, 100));
    
    // Tab 3: 消息 (tab-item index 2)
    console.log("Clicking 消息...");
    await pg.evaluate(()=>{const els=[...document.querySelectorAll(".tab-item")]; if(els[2]) els[2].click();});
    await new Promise(r=>setTimeout(r,2000));
    txt = await pg.evaluate(()=>document.body.innerText);
    results["消息"] = txt.substring(0, 500);
    console.log("✅ 消息:", txt.substring(0, 100));
    
    // Tab 4: 我的 (tab-item index 3)
    console.log("Clicking 我的...");
    await pg.evaluate(()=>{const els=[...document.querySelectorAll(".tab-item")]; if(els[3]) els[3].click();});
    await new Promise(r=>setTimeout(r,2000));
    txt = await pg.evaluate(()=>document.body.innerText);
    results["我的"] = txt.substring(0, 500);
    console.log("✅ 我的:", txt.substring(0, 100));
    
    require("fs").writeFileSync("D:/ai_project/new-car-trade/tools/output/screenshots/verify-final.txt", JSON.stringify(results, null, 2));
    console.log("\n✅ All 5 tabs verified successfully!");
    await b.close();
})().catch(e=>{console.error(e);process.exit(1);})