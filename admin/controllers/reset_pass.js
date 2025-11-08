let {db, storage, Timestamp} = require('../config/connection');
let nodemailer = require('nodemailer');
let crypto = require('crypto')
var transporter = nodemailer.createTransport({
    service: 'gmail', auth: {
        user: 'hopelessornohope@gmail.com', pass: 'nbjjqoxzykcypear'
    }
});


module.exports.getRequest = async (req, res) => {
    try {
        var list = [];
        let dataQuery = await db.collection("forget_pass")
            .orderBy('created_at', "desc")
            .get();

        if (!dataQuery.empty) {
            await dataQuery.forEach((collection) => {
                let obj = {id: collection.id}
                Object.assign(obj, collection.data());

                // let date = new Date(obj.created_at.toMillis());
                obj.created_at = collection.data().created_at ? new Date(obj.created_at.toMillis()).toLocaleString() : "";

                list.push(obj);
            })
            res.send(JSON.stringify(list));
        } else {
            res.send({status: 404, message: "Không có dữ liệu"});
        }
    } catch (e) {
        res.send({status: 500, message: "Hệ thống lỗi, vui lòng thử lại sau"});
    }
}
module.exports.sendMail = async (req, res) => {
    try {

        let info = await db.collection('forget_pass')
            .doc(req.params.requestId)
            .get();
        let users = await db.collection('user')
            .where('email', '==', info.data().email)
            .where('username', '==', info.data().username)
            .select('email', 'username')
            .get();
        const generatePassword = (length = 20, characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~!@-#$') => Array.from(crypto.randomFillSync(new Uint32Array(length)))
            .map((x) => characters[x % characters.length])
            .join('');
        let password = generatePassword();
        if (info.exists && users.size > 0) {
            await users.forEach((collection) => {
                db.collection('user').doc(collection.id).update({
                    'password': password
                })
            });
            var mailOptions = {
                from: 'hopelessornohope@gmail.com',
                to: info.data().email,
                subject: 'Thông báo mật khẩu mới cho app đọc sách WeBook',
                html: '<h2>Hello,</h2><br/><span>Mật khẩu app WeBook cho tài khoản ' + info.data().username + ' đã được khởi tạo lại.</span>' + '<br/>' + '<span>Mật khẩu mới là ' + password + '</span>' + '<br/>' + '<br/>' + '<span>Thân ái,</span>' + '<br/>' + '<br/>' + '<span>WeBook team.</span>'
            };
            await transporter.sendMail(mailOptions, function (error, info) {
                if (error) {
                    console.log(error);
                } else {
                    console.log('Email sent: ' + info.response);
                }
            });
            await db.collection('forget_pass').doc(info.id).delete();
            res.send({status: 200, message: "Đổi mật khẩu và gửi mật khẩu thành công"});

        } else {
            await db.collection('forget_pass').doc(info.id).delete();
            res.send({status: 404, message: "Tài khoản này không có"});
        }
    } catch (e) {
        res.send({status: 500, message: "Hệ thống đang xảy ra lỗi, vui lòng thử lại"});
    }

}
module.exports.deleteRequest = async (req, res) => {
    try {
        let id = req.params.requestId
        await db.collection('forget_pass')
            .doc(id)
            .delete();
        res.redirect('../');
    } catch (e) {
        console.log(e);
    }
}
