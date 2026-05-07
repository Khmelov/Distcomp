const express = require("express")

const newsRouter = express.Router()

let news = []
let id = 1

const getNextId = () => {
    if (news.length === 0) return 1;
    
    const maxId = Math.max(...news.map(newNew => newNew.id));
    
    const allIds = news.map(newNew => newNew.id).sort((a, b) => a - b);
    for (let i = 1; i <= maxId + 1; i++) {
        if (!allIds.includes(i)) {
            return i;
        }
    }
    return maxId + 1;
};

newsRouter.get('/news', (req, res) => {
    return res.status(200).send([...news])
})

newsRouter.put('/news', (req, res) => {
    const newsBody = req.body
    const id = newsBody.id

    if (id) {
        return res.status(422).send({})
    }

    const findNew = news.find(newObj => newObj.id == id)
    const newNews = {...findNew, id, ...newsBody}

    news = news.map(newObj => {
        if (newObj.id == id) {
            return {...newNews}
        }
        return {...newObj}
    })

    return res.status(200).send({ ...newNews })
})

newsRouter.post('/news', (req, res) => {
    const newNews = {id: getNextId(), ...req.body}
    news.push(newNews)
    id++
    return res.status(201).send(newNews)
})

newsRouter.put('/news/:id', (req, res) => {
    const newsBody = req.body
    const id = req.params.id
    const findNew = news.find(newObj => newObj.id == id)

    if (!findNew) {
        return res.status(404).send({})
    }

    const newNews = {...findNew, id, ...newsBody}

    news = news.map(newObj => {
        if (newObj.id == id) {
            return {...newNews}
        }
        return {...newObj}
    })

    return res.status(200).send({ ...newNews })
})

newsRouter.get('/news/:id', (req, res) => {
    const id = req.params.id
    const findNew = news.find(newObj => newObj.id == id)
    return res.status(200).send({ ...findNew })
})

newsRouter.delete('/news/:id', (req, res) => {
    const id = req.params.id
    const findNew = news.find(newObj => newObj.id == id) 

    if (!findNew) {
        return res.status(404).send({})
    }
    news = news.filter(newObj => {
        return newObj.id != id
    })
    return res.status(204).send({})
})

module.exports = newsRouter
