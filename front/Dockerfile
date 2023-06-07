FROM node:18.6.0-alpine

WORKDIR /app

EXPOSE 8080

COPY ["package.json", "package-lock.json*", "./"]

RUN npm install

COPY . .

CMD ["npm", "run", "serve", "--", "--port", "8080"]
