'use strict'
module.exports.timeStamp2Locale = (timestamp) => {
    return new Date(timestamp.toMillis()).toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

module.exports.number2LocaleCurrency = (balance) => {
    return parseInt(balance).toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})
}

