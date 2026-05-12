const express = require('express');
const router = express.Router();
const store = require('../store');

function validateLogin(login) {
    if (typeof login !== 'string' || login.length < 2 || login.length > 64) return false;
    return true;
}

function validatePassword(pw) {
    if (typeof pw !== 'string' || pw.length < 8 || pw.length > 128) return false;
    return true;
}

function validateName(name) {
    if (typeof name !== 'string' || name.length < 2 || name.length > 64) return false;
    return true;
}

router.post('/', (req, res) => {
    const { login, password, firstname, lastname } = req.body;

    if (!login || !password || !firstname || !lastname) {
        return res.status(400).json({ errorMessage: 'All fields required', errorCode: 40001 });
    }
    if (!validateLogin(login)) {
        return res.status(400).json({ errorMessage: 'Login must be 2-64 chars', errorCode: 40002 });
    }
    if (!validatePassword(password)) {
        return res.status(400).json({ errorMessage: 'Password must be 8-128 chars', errorCode: 40003 });
    }
    if (!validateName(firstname) || !validateName(lastname)) {
        return res.status(400).json({ errorMessage: 'Firstname/Lastname must be 2-64 chars', errorCode: 40004 });
    }

    const result = store.createCreator(req.body);
    if (result.error) {
        return res.status(403).json({ errorMessage: result.error, errorCode: result.code });
    }
    res.status(201).json(result);
});

router.get('/', (req, res) => res.json(store.getAllCreators()));

router.get('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });

    const creator = store.getCreator(id);
    if (!creator) return res.status(404).json({ errorMessage: 'Creator not found', errorCode: 40401 });
    res.json(creator);
});

router.put('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });

    const updated = store.updateCreator(id, req.body);
    if (!updated) return res.status(404).json({ errorMessage: 'Creator not found', errorCode: 40401 });
    if (updated.error) return res.status(403).json({ errorMessage: updated.error, errorCode: updated.code });
    res.json(updated);
});

router.delete('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });

    store.deleteCreator(id)
        ? res.status(204).send()
        : res.status(404).json({ errorMessage: 'Creator not found', errorCode: 40401 });
});

module.exports = router;