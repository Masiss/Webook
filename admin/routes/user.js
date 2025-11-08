var express = require('express');
const {
    getUsers,
    storeUser,
    deleteUser,
    detailUser,
    updateBalance
} = require("../controllers/user");
var router = express.Router();
var multer = require('multer');
var upload = multer();

/* GET users listing. */


router.get('/', (req, res) => {
    res.render('user/index');
})
router.post('/', getUsers);
router.get('/create', (req, res) => {
    res.render('user/create');
});
router.post('/store', upload.none(), storeUser);
router.get('/:userId/detail', detailUser);
router.post('/:userId/updateBalance', updateBalance);
router.post('/:userId/delete', deleteUser);


module.exports = router;
