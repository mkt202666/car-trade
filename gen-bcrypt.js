const bcrypt = require('bcryptjs');
const newHash = bcrypt.hashSync('123456', 10);
console.log('original ($2b$):', newHash);
const with2a = '$2a$10$' + newHash.substring(7);
console.log('as $2a$:', with2a);
console.log('verify 123456 against 2a:', bcrypt.compareSync('123456', with2a));
