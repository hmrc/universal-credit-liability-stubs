const getRandomElement = (arr) => {
  return arr[Math.floor(Math.random() * arr.length)];
}

const randomNino = () => {
  const number = `${Math.floor(Math.random() * 1000000)}`.padStart(6, '0');
  return `AA${number}`;
}

const randomInvalidNino = () => {
  const number = `${Math.floor(Math.random() * 100000000)}`.padStart(8, '0');
  return `ZZ${number}`;
}

const randomUniversalCreditRecordType = () => {
  return getRandomElement(['UC', 'LCW/LCWRA']);
}

module.exports = {
  randomNino,
  randomInvalidNino,
  randomUniversalCreditRecordType
};
