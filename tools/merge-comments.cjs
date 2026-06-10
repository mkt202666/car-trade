const fs = require('fs');

// 1. 从 init.sql 提取 COMMENT ON 语句，按表名分组
const initSql = fs.readFileSync('D:/ai_project/new-car-trade/docs/init.sql', 'utf-8');
const commentMap = {};
let curTable = null;
for (const line of initSql.split('\n')) {
  const tm = line.match(/^COMMENT ON TABLE (\w+) IS '(.+)';$/);
  if (tm) { curTable = tm[1]; if (!commentMap[curTable]) commentMap[curTable] = []; commentMap[curTable].push(line.trim()); continue; }
  const cm = line.match(/^COMMENT ON COLUMN (\w+)\.(\w+) IS '(.+)';$/);
  if (cm && curTable && cm[1] === curTable) { commentMap[curTable].push(line.trim()); }
}

// 2. 读取 schema.sql
const schemaSql = fs.readFileSync('D:/ai_project/new-car-trade/car-trade-backend/src/main/resources/schema.sql', 'utf-8');
const lines = schemaSql.split('\n');

// 3. 解析 schema.sql，找到每个 CREATE TABLE 块的范围
const result = [];
let i = 0;
let tablesProcessed = new Set();

while (i < lines.length) {
  const line = lines[i];
  
  // 检测 CREATE TABLE 行
  const createMatch = line.match(/^CREATE TABLE(?: IF NOT EXISTS)? (\w+) \(/);
  if (createMatch) {
    const tableName = createMatch[1];
    // 收集 CREATE TABLE 块的所有行直到 );
    result.push(lines[i]);
    i++;
    while (i < lines.length && !lines[i-1].match(/\);\s*$/)) {
      result.push(lines[i]);
      i++;
    }
    // 现在 lines[i-1] 是 ");" 行
    // 继续收集后续的 CREATE INDEX 行
    while (i < lines.length && (lines[i].match(/^CREATE INDEX/) || lines[i].match(/^$/) || lines[i].match(/^--/))) {
      result.push(lines[i]);
      i++;
    }
    
    // 如果这个表在 commentMap 中有注释，插入它们
    if (commentMap[tableName] && !tablesProcessed.has(tableName)) {
      result.push('');  // 空行
      for (const comment of commentMap[tableName]) {
        result.push(comment);
      }
      tablesProcessed.add(tableName);
    }
    continue;
  }
  
  result.push(lines[i]);
  i++;
}

// 4. 处理 ALTER TABLE 新字段的注释（追加到文件末尾或相关位置）
// 找到 car_sources 和 orders 的 COMMENT ON 位置，追加新字段注释
const newCarFields = [
  "COMMENT ON COLUMN car_sources.vin IS '车架号(VIN码)';",
  "COMMENT ON COLUMN car_sources.transmission IS '变速箱类型: MANUAL-手动, AUTO-自动, CVT-无级变速, DCT-双离合';",
  "COMMENT ON COLUMN car_sources.pricing_type IS '报价方式: FIXED-一口价, AUCTION-拍卖';",
  "COMMENT ON COLUMN car_sources.starting_price IS '拍卖起拍价(元)';",
  "COMMENT ON COLUMN car_sources.ceiling_price IS '拍卖封顶价(元)';",
  "COMMENT ON COLUMN car_sources.bid_increment IS '拍卖加价幅度(元)';",
  "COMMENT ON COLUMN car_sources.inspection_report_type IS '检测报告类型: LINK-链接, FILE-文件';",
  "COMMENT ON COLUMN car_sources.inspection_report_url IS '检测报告链接或文件URL';",
  "COMMENT ON COLUMN car_sources.certificate_materials IS '证件材料JSON，含行驶证/产权证/车身铭牌等';",
  "COMMENT ON COLUMN car_sources.support_lock_negotiation IS '是否支持锁车洽谈';",
  "COMMENT ON COLUMN car_sources.ai_auto_promote IS '是否开启AI自动推广';",
  "COMMENT ON COLUMN car_sources.is_draft IS '是否为草稿状态';",
  "COMMENT ON COLUMN car_sources.export_countries IS '出口国家编码，逗号分隔，如 RU,KZ,AE';",
];

const newOrderFields = [
  "COMMENT ON COLUMN orders.contract_content IS '合同内容文本';",
  "COMMENT ON COLUMN orders.contract_submitted IS '合同是否已提交';",
  "COMMENT ON COLUMN orders.contract_submitted_at IS '合同提交时间';",
  "COMMENT ON COLUMN orders.contract_confirmed IS '合同是否已确认';",
  "COMMENT ON COLUMN orders.contract_confirmed_at IS '合同确认时间';",
  "COMMENT ON COLUMN orders.terminate_count IS '当日终止交易发起次数';",
  "COMMENT ON COLUMN orders.terminate_limit IS '每日终止交易次数上限';",
  "COMMENT ON COLUMN orders.terminate_reason IS '终止交易原因';",
  "COMMENT ON COLUMN orders.last_terminate_at IS '最后发起终止交易时间';",
];

// 5. 拍卖表注释
const auctionComments = [
  "",
  "COMMENT ON TABLE auctions IS '拍卖表';",
  "COMMENT ON COLUMN auctions.id IS '主键ID';",
  "COMMENT ON COLUMN auctions.car_id IS '关联车源ID';",
  "COMMENT ON COLUMN auctions.seller_id IS '卖家用户ID';",
  "COMMENT ON COLUMN auctions.start_price IS '起拍价(元)';",
  "COMMENT ON COLUMN auctions.reserve_price IS '保留价/底价(元)';",
  "COMMENT ON COLUMN auctions.current_price IS '当前最高出价(元)';",
  "COMMENT ON COLUMN auctions.bid_increment IS '每次加价幅度(元)';",
  "COMMENT ON COLUMN auctions.start_time IS '拍卖开始时间';",
  "COMMENT ON COLUMN auctions.end_time IS '拍卖计划结束时间';",
  "COMMENT ON COLUMN auctions.actual_end_time IS '拍卖实际结束时间';",
  "COMMENT ON COLUMN auctions.status IS '状态: PENDING-待开始, BIDDING-竞拍中, ENDED-已结束, SETTLED-已结算, CANCELLED-已取消, FAILED-流拍';",
  "COMMENT ON COLUMN auctions.winner_id IS '中标者用户ID';",
  "COMMENT ON COLUMN auctions.winning_price IS '中标价格(元)';",
  "COMMENT ON COLUMN auctions.total_bids IS '累计出价次数';",
  "COMMENT ON COLUMN auctions.view_count IS '浏览次数';",
  "COMMENT ON COLUMN auctions.created_at IS '创建时间';",
  "COMMENT ON COLUMN auctions.updated_at IS '更新时间';",
  "COMMENT ON COLUMN auctions.version IS '乐观锁版本号';",
  "",
  "COMMENT ON TABLE auction_bids IS '拍卖出价记录表';",
  "COMMENT ON COLUMN auction_bids.id IS '主键ID';",
  "COMMENT ON COLUMN auction_bids.auction_id IS '关联拍卖ID';",
  "COMMENT ON COLUMN auction_bids.bidder_id IS '出价者用户ID';",
  "COMMENT ON COLUMN auction_bids.bid_price IS '出价金额(元)';",
  "COMMENT ON COLUMN auction_bids.bid_time IS '出价时间';",
  "COMMENT ON COLUMN auction_bids.is_winning IS '是否为当前最高出价';",
  "COMMENT ON COLUMN auction_bids.created_at IS '创建时间';",
  "",
  "COMMENT ON TABLE auction_watches IS '拍卖关注表';",
  "COMMENT ON COLUMN auction_watches.id IS '主键ID';",
  "COMMENT ON COLUMN auction_watches.auction_id IS '关联拍卖ID';",
  "COMMENT ON COLUMN auction_watches.user_id IS '关注用户ID';",
  "COMMENT ON COLUMN auction_watches.created_at IS '关注时间';",
];

// 找到合适的位置插入新字段注释和拍卖表注释
// 策略：在 car_sources 的 COMMENT 块后面追加新字段注释
// 在 orders 的 COMMENT 块后面追加新字段注释
// 在 auction_watches 的 CREATE INDEX 后面追加拍卖表注释

let finalResult = [];
let afterCarSourcesComments = false;
let afterOrdersComments = false;
let afterAuctionWatches = false;

for (let j = 0; j < result.length; j++) {
  finalResult.push(result[j]);
  
  // 在 car_sources 最后一行 COMMENT 后追加新字段注释
  if (result[j].includes("COMMENT ON COLUMN car_sources.deleted_at")) {
    for (const c of newCarFields) finalResult.push(c);
  }
  
  // 在 orders 最后一行 COMMENT 后追加新字段注释
  if (result[j].includes("COMMENT ON COLUMN orders.updated_at IS '更新时间'")) {
    for (const c of newOrderFields) finalResult.push(c);
  }
  
  // 在 auction_watches 的 CREATE INDEX 后追加拍卖表注释
  if (result[j].includes("CREATE INDEX IF NOT EXISTS idx_auction_watches_user_id")) {
    for (const c of auctionComments) finalResult.push(c);
  }
}

// 6. 写入文件
fs.writeFileSync('D:/ai_project/new-car-trade/car-trade-backend/src/main/resources/schema.sql', finalResult.join('\n'), 'utf-8');

console.log('Done! Tables with comments: ' + tablesProcessed.size);
console.log('Total lines: ' + finalResult.length);
