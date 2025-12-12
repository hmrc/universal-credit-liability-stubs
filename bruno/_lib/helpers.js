
const randomNino = () => {
    const number = `${Math.floor(Math.random() * 100000)}`.padStart(6, '0');
    return `AE${number}`;
}

module.exports = {
    randomNino
};
