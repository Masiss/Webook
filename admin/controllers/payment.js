let {db, storage, Timestamp} = require('../config/connection');


module.exports.getPayment = async (req, res) => {
    try {
        if (!req.body.filter) {
            res.send([]);
        }
        let filter = {
            1: true,
            2: false
        };
        let query = db.collection('payment');
        let listPayment = !filter.hasOwnProperty(req.body.filter) ?
            await query.get() :
            (
                filter[req.body] ?
                    await query.where('is_accepted', '==', true).get() :
                    await query.where('is_accepted', '==', false).get()
            );
        let payments = [];
        listPayment.forEach((collection) => {
            let obj = collection.data();
            obj.id = collection.id;
            obj.amount = parseInt(obj.amount).toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
            payments.push(obj);
        })
        for (let payment of payments) {
            payment.username = await getUserDataForPayment(payment.userId).then((userData) => {
                return userData.username;
            });
        }
        res.send(200, JSON.stringify(payments));
    } catch (e) {
        console.log(e)
    }
}
module.exports.acceptPayment = async (req, res) => {
    try {

        let id = req.params.paymentId;
        let payment = await db.collection('payment').doc(id).get();
        let user = await getUserDataForPayment(payment.data().userId);
        if (payment.data().hasOwnProperty('is_accepted') && payment.data().is_accepted === false) {
            await db.collection('user')
                .doc(payment.data().userId)
                .update({
                    balance: parseInt(user.balance) + parseInt(payment.data().amount)
                });
            await db.collection('payment')
                .doc(id)
                .update({
                    is_accepted: true
                });
        } else {
            return res.status(404).send({message: "Giao dịch đã được xử lí"});
        }
        return res.status(200).send({message: "Cập nhật số dư thành công"});
    } catch (e) {
        return res.status(500).send({message: "Hệ thống lỗi"});
    }
}
module.exports.deletePayment = async (req, res) => {
    try {
        let id = req.params.paymentId;
        await db.collection('payment')
            .doc(id)
            .delete();
        res.send({status: 200, message: "Xoá thành công"});
    } catch (e) {

    }

}

async function getUserDataForPayment(userId) {
    let user = await db.collection('user').doc(userId).get();
    return user.data();
}
