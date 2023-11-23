var express = require('express')
const {
    searchBook,
    getBook,
    publishMultiBook,
    detailBook,
    deleteBook,
    storeBook,
    editBook,
    updateBook,
    readBook,
    publishSingleBook, importExcel
} = require("../controllers/books");
var router = express.Router()
var multer = require('multer');
var upload = multer();


router.get('/', async (req, res) => {
    let id = await req.query.id;
    let mess = await req.query.message;
    let message = {};
    if (id) {
        if (id.substring(id.length - 3) == 200) {
            if (mess == 'create') {
                message.content = "Thêm sách thành công";
            } else if (mess == 'update') {
                message.content = "Cập nhật sách thành công";
            } else {
                message.content = "Xoá thành công";
            }
            message.id = 200;
        } else {
            message.content = "Xoá không thành công"
            message.id = 501;
        }
    } else {
        message = null;
    }
    res.render('book/index', {message: message});

});

router.get('/index', (req, res) => {
    res.redirect('./');
})
router.post('/', async (req, res) => {
    if (req.body.filter && parseInt(req.body.filter) !== 0) {
        books = await searchBook(req.body.filter);
    } else {
        books = await getBook()

    }
    res.send(JSON.stringify(books));
    // res.send(books);
});
router.post('/publish', publishMultiBook);
router.get('/:bookId/detail', detailBook)

router.post('/:bookId/delete', deleteBook)
router.get('/create', (req, res) => {
    res.render('book/create');
})

router.post('/store', upload.fields([{name: 'image', maxCount: 1}, {name: 'content', maxCount: 1}]), storeBook);


router.get('/:bookId/edit', editBook)
router.post('/:bookId/update', upload.fields([{name: 'image', maxCount: 1}, {
    name: 'content',
    maxCount: 1
}]), updateBook);
router.get('/:bookId/read', readBook);
router.post('/:bookId/publish', publishSingleBook)

var storageMulter = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, "uploads");
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + "-" + file.originalname);
    },
});
upload = multer({storage: storageMulter});

router.post('/import', upload.single('file'), importExcel);

module.exports = router
