let {db, storage, Timestamp} = require('../config/connection');
let {randomUUID} = require('crypto');
let xlsx = require('xlsx');
let fs = require('fs')
const {timeStamp2Locale, number2LocaleCurrency} = require('../utils/dataToLocale');

module.exports.getBook = async () => {
    try {
        var books = [];
        let dataQuery = await db.collection("book").get();
        if (dataQuery.empty) {
            console.log('data k có');
        } else {
            let promises = [];
            for (let collection of dataQuery.docs) {
                let book = await collection2Book(collection);
                books.push(book);
            }
            // await dataQuery.forEach((collection) => {
            //     let obj = {id: collection.id}
            //     Object.assign(obj, collection.data());
            //     obj.image = collection.data().image ? gsToHttps(obj.image) : "";
            //     obj.created_at = timeStamp2Locale(obj.created_at);
            //     obj.price = number2LocaleCurrency(obj.price);
            //     obj.sold = collection.data().sold ? obj.sold : "";
            //     books.push(obj);
            // })
            // await Promise.all(promises).then((val) => {
            //     for (let i = 0; i < promises.length; i++) {
            //         books[i].image = val[i];
            //     }
            // })
            return books;

        }
    } catch (e) {
        console.log(e);
    }
}

async function collection2Book(collection) {
    let book = {id: collection.id}
    Object.assign(book, collection.data());
    book.price = number2LocaleCurrency(book.price);
    book.image = book.image ? await gsToHttps(book.image) : "";
    book.created_at = timeStamp2Locale(book.created_at);
    book.sold = book.sold ? book.sold : "";
    return book;
}

module.exports.searchBook = async (input) => {
    if (isNaN(parseInt(input))) {
        return [];
    }
    let books = [];
    let filter = {
        1: true, 2: false,
    };
    let data = await db.collection('book')
        .where('is_published', '==', filter[input])
        .get();
    if (!data.empty) {
        let promises = [];
        for (let book of data.docs) {
            let obj = await collection2Book(book);
            books.push(obj);
        }
        return books;

    } else {
        return null;
    }

}


module.exports.detailBook = async function (req, res) {
    var id = req.params.bookId;
    let collection = await db.collection('book')
        .doc(id).get();
    let book = await collection2Book(collection)
    res.render('book/detail', {book: book});
}

module.exports.deleteBook = async function (req, res) {
    try {
        var id = req.params.bookId;
        let users = await db.collection('user').get();
        let book = await db.collection('book')
            .doc(id)
            .get();
        let is_sold = book.data().sold;
        let is_published = book.data().is_published;
        if (is_sold === 0 || !is_published) {
            await db.collection('book')
                .doc(id).delete();
            res.status(200).send({message: "Xoá sách thành công"});
        } else {
            res.status(500).send({message: "Sách đã được bán, không thể xoá"});
        }
    } catch (e) {
        res.redirect('../error');
    }
}
module.exports.storeBook = async function (req, res) {
    try {
        var formData = await req.body;
        formData = Object.entries(formData);
        if (!req.files.image || !req.files.content) {
            res.redirect('back');
        }
        var image = await req.files.image[0];
        var content = await req.files.content[0];
        console.log(image);
        let book = {};
        if (formData.find(([key, val]) => val === "") || !image || !content) {
            res.redirect('back')
        } else {
            await formData.forEach(([key, val]) => {
                book[key] = val;
            });
            const blob = await Buffer.from(image.buffer);
            const blob1 = await Buffer.from(content.buffer);
            let imageName = image.originalname.split('.');
            let contentName = content.originalname.split('.');
            let fileName = randomUUID() + "." + imageName[1];
            let fileName1 = randomUUID() + "." + contentName[1];

            let imageLink = await storage.file('book/' + fileName).save(blob, {
                contentType: image.mimetype
            });
            let imageLink1 = await storage.file('book/' + fileName1).save(blob1, {
                contentType: content.mimetype
            });

            let link = await storage.file('book/' + fileName).cloudStorageURI;
            let link1 = await storage.file('book/' + fileName1).cloudStorageURI;

            book.image = await link.href;
            book.content = await link1.href;
            book.created_at = await Timestamp.now();
            book.sold = 0;
            book.price = parseInt(book.price)
            let docRef = await db.collection('book').add(book);
            docRef = docRef.id;
            await successAction(res, 'create', docRef, true);
        }
    } catch (e) {
        console.log(e)
    }
}
const successAction = (res, message, id, bool = true) => {
    if (bool) {
        res.redirect('./?id=' + id + 200 + '&message=' + message)
    } else {
        res.redirect('../error');
    }

}
const gsToHttps = async (gsLink) => {
    let arrLink = await gsLink.split('/');
    let result = await storage.file('book/' + arrLink[4]).getSignedUrl({
        action: 'read', expires: Date.now() + 60 * 60 * 1000
    });
    let signedUrl = result[0];
    return signedUrl;
}
module.exports.importExcel = async (req, res, next) => {
    try {
        let path = req.file.path;
        var workbook = await xlsx.readFile(path);
        console.log(req.file);
        var sheet_name_list = workbook.SheetNames;
        let jsonData = xlsx.utils.sheet_to_json(workbook.Sheets[sheet_name_list[0]]);
        if (jsonData.length === 0) {
            return res.status(400).json({
                success: false, message: "xml sheet has no data",
            });
        }
        let batch = db.batch();
        await jsonData.forEach((data) => {
            data.price = parseInt(data.price);
            data.sold = 0;
            data.is_published = false;
            data.created_at = Timestamp.now();
            batch.set(db.collection('book').doc(), data);
        })
        await batch.commit()
            .then(() => {
                fs.unlinkSync(path);
            }).then(() => {
                let message = {
                    message: "", // `Thêm ${jsonData.length} sách thành công`,
                    status: 201
                };
                return res.redirect(`./?code=200&message=Thêm ${jsonData} sách thành công`);
            })
    } catch (err) {
        return res.status(500).json({success: false, message: err.message});
    }
}

module.exports.editBook = async (req, res) => {
    let id = req.params.bookId;
    let data = await db.collection('book')
        .doc(id)
        .get();
    let obj = await collection2Book(data);
    obj.price = data.data().price;
    res.render('book/edit', {book: obj});
}


module.exports.updateBook = async (req, res) => {
    try {
        var formData = await req.body;
        formData = Object.entries(formData);
        let id = req.params.bookId;
        console.log(req.files);
        let image = req.files.image ? req.files.image[0] : "";
        let content = req.files.content ? req.files.content[0] : "";
        let book = {};
        console.log(req.formData);
        console.log(formData);
        await formData.forEach(([key, val]) => {
            book[key] = val;
        });
        if (image) {
            const blob = await Buffer.from(image.buffer);
            let imageName = image.originalname.split('.');
            let fileName = randomUUID() + "." + imageName[1];

            let imageLink = await storage.file('book/' + fileName).save(blob, {
                contentType: image.mimetype
            });
            let link = await storage.file('book/' + fileName).cloudStorageURI;
            book.image = await link.href;
        }
        if (content) {
            const blob1 = await Buffer.from(content.buffer);
            let contentName = content.originalname.split('.');
            let fileContentName = randomUUID() + "." + contentName[1];
            console.log(content);
            let contentLink = await storage.file('book/' + fileContentName).save(blob1, {
                contentType: content.mimetype
            });
            let contentink1 = await storage.file('book/' + fileContentName).cloudStorageURI;
            book.content = await contentink1.href;
        }
        book.updated_at = await Timestamp.now();
        let docRef = await db.collection('book')
            .doc(id)
            .update(book);
        docRef = docRef.id;
        res.redirect(`../?id=${id}200&message=update`)

    } catch (e) {
        console.log(e);
        // res.render('./error');
    }
}
module.exports.readBook = (req, res) => {
    try {

        let id = req.params.bookId;
        db.collection('book')
            .doc(id)
            .get()
            .then((book) => {
                gsToHttps(book.data().content)
                    .then((link) => {
                        res.render('book/read', {content: link});
                    });
            });
    } catch (e) {
        res.redirect('../error');
    }

}

module.exports.chartBook = async (req, res) => {
    let data = [];
    let promises = []
    let arr = addDateForLabels('book');
    let date = arr.map(obj => {
        return Object.keys(obj)[0];
    })
    let arrUser = addDateForLabels('user');
    let listUsers = await db.collection('user')
        .get();
    for (let user of listUsers.docs) {
        let listPurchase = await db.collection('user')
            .doc(user.id)
            .collection('purchase_log')
            .get();
        for (let purchase of listPurchase.docs) {
            let obj = purchase.data();
            obj.time = new Date(obj.time.toMillis()).toLocaleString('vi-VN', {
                year: 'numeric', month: '2-digit', day: '2-digit'
            });
            if (date.includes(obj.time)) {
                arr.some((x) => {
                    if (x.hasOwnProperty(obj.time)) {
                        x[obj.time].quantity += 1;
                        let revenue = db.collection('book')
                            .doc(obj.book).get().then((collection) => {
                                return collection.data().price;
                            });
                        promises.push({[obj.time]: revenue});
                    }
                });
            }

        }

    }
    listUsers.forEach((user) => {
        if (user.data().created_at) {
            let created_at = timeStamp2Locale(user.data().created_at)
            arrUser.some(a => {
                if (a.hasOwnProperty(created_at)) {
                    a[created_at] += 1;
                }
            })
        }
    })
    arr = await pushRevenue2Arr(arr, promises);
    res.send(JSON.stringify({book: arr, user: arrUser}));

}


async function pushRevenue2Arr(arr, promises) {
    for (let i = 0; i < promises.length; i++) {
        let promise = Promise.resolve(Object.values(promises[i])[0]);
        let key = Object.keys(promises[i])[0];
        await Promise.all([promise]).then((val) => {
            let price = val[0];
            // arr[key].revenue += price;
            arr.some(x => {
                if (Object.keys(x)[0] === key) {
                    x[key].revenue += parseInt(price)
                }
            });
        })
    }
    return arr;

}

function addDateForLabels(type) {
    let listDate = [];
    var date = new Date();
    var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);

    for (var i = new Date(firstDay); i <= lastDay; i.setDate(i.getDate() + 1)) {
        let a = i.toLocaleString('vi-VN', {
            year: 'numeric', month: '2-digit', day: '2-digit'
        });
        if (type === 'book') {
            listDate.push({[a]: {revenue: 0, quantity: 0}});

        } else if (type === 'user') {
            listDate.push({[a]: 0})
        }
    }

    return listDate

}

module.exports.publishMultiBook = async (req, res) => {
    let listId = req.body.listBooks;
    let unableList = [];
    try {

        for (let id of listId) {
            let book = await db.collection('book')
                .doc(id)
                .get()
            if (book.data().hasOwnProperty('content') &&
                book.data().hasOwnProperty('image') &&
                (!book.data().hasOwnProperty('is_published') || !book.date().is_published)) {
                await db.collection('book').doc(id).update('is_published', true);
            } else {
                unableList.push(book.data().title);
            }
        }
        if (unableList.length === 0) {
            res.send(JSON.stringify({status: 200, message: 'Cập nhật thành công'}));

        } else {
            res.send(JSON.stringify({status: 200, message: `Sách ${unableList.join(", ")} chưa đủ thông tin`}));
        }

    } catch (e) {
        console.log(e);
    }
}
module.exports.publishSingleBook = async (req, res) => {
    let id = req.params.bookId;
    try {
        let book = await db.collection('book')
            .doc(id)
            .get()
        if (canBePublish(book.data())) {
            await db.collection('book').doc(id).update('is_published', true);
            res.send(JSON.stringify({status: 200, message: 'Cập nhật thành công'}));

        } else {
            res.status(400).send("Sách chưa đủ thông tin để đăng");
        }
    } catch (e) {
        console.log(e);
    }
}

function canBePublish(book) {
    return (book.hasOwnProperty('content') && book.hasOwnProperty('image') && (!book.hasOwnProperty('is_published') || !book.is_published));
}
