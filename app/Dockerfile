FROM node

COPY package.json ./
COPY public ./public
COPY src ./src

RUN npm install

CMD ["npm", "start"]