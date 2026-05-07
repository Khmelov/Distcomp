const express = require("express")

const usersRouter = express.Router()

let users = []
let id = 1

const getNextId = () => {
    if (users.length === 0) return 1;
    
    const maxId = Math.max(...users.map(user => user.id));
    
    const allIds = users.map(user => user.id).sort((a, b) => a - b);
    for (let i = 1; i <= maxId + 1; i++) {
        if (!allIds.includes(i)) {
            return i;
        }
    }
    return maxId + 1;
};

usersRouter.get('/users', (req, res) => {
    return res.status(200).send([...users])
})

usersRouter.post('/users', (req, res) => {
    const newUser = {id: getNextId(), ...req.body}
    users.push(newUser)
    id++
    return res.status(201).send(newUser)
})

usersRouter.put('/users', (req, res) => {
    const userBody = req.body
    const id = userBody.id

    if (id) {
        return res.status(422).send({})
    }

    const user = users.find(user => user.id == id)
    const newUser = {...user, id, ...userBody}

    users = users.map(user => {
        if (user.id == id) {
            return {...newUser}
        }
        return {...user}
    })

    return res.status(200).send({ ...newUser })
})

usersRouter.put('/users/:id', (req, res) => {
    const userBody = req.body
    const id = req.params.id
    const user = users.find(user => user.id == id)

    if (!user) {
        return res.status(404).send({})
    }

    const newUser = {...user, id, ...userBody}

    users = users.map(user => {
        if (user.id == id) {
            return {...newUser}
        }
        return {...user}
    })

    return res.status(200).send({ ...newUser })
})

usersRouter.get('/users/:id', (req, res) => {
    const id = req.params.id
    const user = users.find(user => user.id == id)
    return res.status(200).send({ ...user })
})

usersRouter.delete('/users/:id', (req, res) => {
    const id = req.params.id
    const user = users.find(user => user.id == id) 

    if (!user) {
        return res.status(404).send({})
    }
    users = users.filter(user => {
        return user.id != id
    })
    return res.status(204).send({})
})

module.exports = usersRouter
