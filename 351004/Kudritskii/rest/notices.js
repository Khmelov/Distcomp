const express = require("express")

const noticesRouter = express.Router()

let notices = []
let id = 1

const getNextId = () => {
    if (notices.length === 0) return 1;
    
    const maxId = Math.max(...notices.map(notice => notice.id));
    
    const allIds = notices.map(notice => notice.id).sort((a, b) => a - b);
    for (let i = 1; i <= maxId + 1; i++) {
        if (!allIds.includes(i)) {
            return i;
        }
    }
    return maxId + 1;
};

noticesRouter.get('/notices', (req, res) => {
    return res.status(200).send([...notices])
})

noticesRouter.put('/notices', (req, res) => {
    const noticeBody = req.body
    const id = newsBody.id

    if (id) {
        return res.status(422).send({})
    }

    const findNotice = notices.find(notice => notice.id == id)
    const newNotice = {...findNotice, id, ...noticeBody}

    notices = notices.map(notice => {
        if (notice.id == id) {
            return {...newNotice}
        }
        return {...notice}
    })

    return res.status(200).send({ ...newNotice })
})

noticesRouter.post('/notices', (req, res) => {
    const newNotice = {id: getNextId(), ...req.body}
    notices.push(newNotice)
    id++
    return res.status(201).send(newNotice)
})

noticesRouter.put('/notices/:id', (req, res) => {
    const noticeBody = req.body
    const id = req.params.id
    const findNotice = notices.find(notice => notice.id == id)

    if (!findNotice) {
        return res.status(404).send({})
    }

    const newNotice = {...findNotice, id, ...noticeBody}

    notices = notices.map(notice => {
        if (notice.id == id) {
            return {...newNotice}
        }
        return {...notice}
    })

    return res.status(200).send({ ...newNotice })
})

noticesRouter.get('/notices/:id', (req, res) => {
    const id = req.params.id
    const findNotice = notices.find(notice => notice.id == id)
    return res.status(200).send({ ...findNotice })
})

noticesRouter.delete('/notices/:id', (req, res) => {
    const id = req.params.id
    const findNotice = notices.find(notice => notice.id == id) 

    if (!findNotice) {
        return res.status(404).send({})
    }
    notices = notices.filter(notice => {
        return notice.id != id
    })
    return res.status(204).send({})
})

module.exports = noticesRouter
