const {initializeApp, applicationDefault, cert} = require('firebase-admin/app');
const {getFirestore, Timestamp, FieldValue, Filter} = require('firebase-admin/firestore');
const serviceAccount = require('../config/firebase.json');
const {getStorage,getDownloadURL} = require('firebase-admin/storage');
const bucket = "ebook-app-7e7d7.appspot.com";

initializeApp({
    credential: cert(serviceAccount),
    storageBucket: bucket
});


const db = getFirestore();
const storage = getStorage().bucket();

module.exports = {db, storage,Timestamp};
