var express = require('express')
const {getPayment, acceptPayment, deletePayment} = require("../controllers/payment");
const router = express.Router();
router.get('/', (req, res) => {
    res.render('payment/index')
})
router.post('/', getPayment)
router.post('/:paymentId/accept', acceptPayment)
router.post('/:paymentId/delete', deletePayment)
module.exports = router
