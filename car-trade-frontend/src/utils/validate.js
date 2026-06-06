export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

export function isValidPrice(price) {
  return !isNaN(price) && Number(price) > 0
}

export function isValidMileage(mileage) {
  return !isNaN(mileage) && Number(mileage) >= 0
}
