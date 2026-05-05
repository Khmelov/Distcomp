const STOP_WORDS = ['spam', 'badword', 'offensive'];

function moderate(content) {
    if (!content) return 'APPROVE';
    for (const word of STOP_WORDS) {
        if (content.toLowerCase().includes(word)) return 'DECLINE';
    }
    return 'APPROVE';
}

module.exports = { moderate };