let {db, storage, Timestamp} = require('../config/connection');
let {randomUUID} = require('crypto');
let xlsx = require('xlsx');
let fs = require('fs')
let {number2LocaleCurrency, timeStamp2Locale} = require('../utils/dataToLocale');

function setupUser(queryCollection) {
    let user = {id: queryCollection.id};
    Object.assign(user, queryCollection.data());
    user.balance = number2LocaleCurrency(user.balance);
    return user;
}

module.exports.getUsers = async (req, res) => {
    try {
        var users = [];
        let dataQuery = await db.collection("user").get();
        if (!dataQuery.empty) {
            for (let user of dataQuery.docs) {
                user = setupUser(user);
                users.push(user);
            }
            res.send(JSON.stringify(users));
        }
    } catch (e) {
        console.log(e);
    }
}
module.exports.detailUser = async (req, res) => {
    let data = await db.collection('user')
        .doc(req.params.userId)
        .get();
    let purchaseList = [];
    let user = setupUser(data);
    let purchase_log = await db.collection('user')
        .doc(req.params.userId)
        .collection('purchase_log')
        .get();
    for (let purchase of purchase_log.docs) {
        let obj = {};
        let book = await db.collection('book').doc(purchase.data().book).get()
        obj.book = book.data().title;
        obj.time = timeStamp2Locale(purchase.data().time);
        purchaseList.push(obj);
    }
    res.render('user/detail', {user: user, purchase_log: purchaseList});
}
module.exports.deleteUser = async function (req, res) {
    var id = req.params.userId;
    try {
        await db.collection('user')
            .doc(id).delete();
        res.redirect('../?id=' + id + 200);

    } catch (e) {
        console.log(e);
        // res.redirect('../error');
    }

}
module.exports.updateBalance = async (req, res) => {
    try {
        let balance = req.body.balance;
        let id = req.params.userId;
        await db.collection('user')
            .doc(id)
            .update('balance', parseInt(balance));
        res.send(200, {message: "Cập nhật thành công"})
    } catch (e) {
        console.log(e);
        res.send(500, {message: "Lỗi đã xảy ra, vui lòng thử lại sau"});
        // res.render('./error');
    }
}
module.exports.storeUser = async function (req, res) {
    try {
        var formData = await req.body;
        formData = Object.entries(formData);
        let book = {};
        if (formData.find(([key, val]) => val === "")) {
            res.redirect('back')
        } else {
            await formData.forEach(([key, val]) => {
                book[key] = val;
            });
            book.created_at = await Timestamp.now();
            let docRef = await db.collection('user').add(book);
            docRef = docRef.id;
            res.redirect('./');
        }
    } catch (e) {
        console.log(e);
    }


}
