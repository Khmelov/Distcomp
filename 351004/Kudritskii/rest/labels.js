const express = require("express")

const labelsRouter = express.Router()

let labels = []
let id = 1

const getNextId = () => {
    if (labels.length === 0) return 1;
    
    const maxId = Math.max(...labels.map(label => label.id));
    
    const allIds = labels.map(label => label.id).sort((a, b) => a - b);
    for (let i = 1; i <= maxId + 1; i++) {
        if (!allIds.includes(i)) {
            return i;
        }
    }
    return maxId + 1;
};

labelsRouter.get('/labels', (req, res) => {
    return res.status(200).send([...labels])
})

labelsRouter.put('/labels', (req, res) => {
    const labelBody = req.body
    const id = newsBody.id

    if (id) {
        return res.status(422).send({})
    }

    const findLabel = labels.find(label => label.id == id)
    const newLabel = {...findLabel, id, ...labelBody}

    labels = labels.map(label => {
        if (label.id == id) {
            return {...newLabel}
        }
        return {...label}
    })

    return res.status(200).send({ ...newLabel })
})

labelsRouter.post('/labels', (req, res) => {
    const newLabel = {id: getNextId(), ...req.body}
    labels.push(newLabel)
    id++
    return res.status(201).send(newLabel)
})

labelsRouter.put('/labels/:id', (req, res) => {
    const labelBody = req.body
    const id = req.params.id
    const findLabel = labels.find(notice => notice.id == id)

    if (!findLabel) {
        return res.status(404).send({})
    }

    const newLabel = {...findLabel, id, ...labelBody}

    labels = labels.map(label => {
        if (label.id == id) {
            return {...newLabel}
        }
        return {...label}
    })

    return res.status(200).send({ ...newLabel })
})

labelsRouter.get('/labels/:id', (req, res) => {
    const id = req.params.id
    const findLabel = labels.find(label => label.id == id)
    return res.status(200).send({ ...findLabel })
})

labelsRouter.delete('/labels/:id', (req, res) => {
    const id = req.params.id
    const findLabel = labels.find(label => label.id == id) 

    if (!findLabel) {
        return res.status(404).send({})
    }
    labels = labels.filter(label => {
        return label.id != id
    })
    return res.status(204).send({})
})

module.exports = labelsRouter
