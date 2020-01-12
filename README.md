
```
docker build -t appx .
docker run --rm -it -p 127.0.0.1:8050:4050 appx
docker rmi appx --force
docker-compose rm appx
docker-compose up
```

