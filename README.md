# user pass
* apiUser
* qwerty

# estimates
* adding geo ip logic 1h
* db creating data validation creation begin 2h
* done loan adding logic 3h
* add black-list 1h
* test initialization create first test 1h
+ 30 min

# how tos
* http://localhost:8080/user/1
* http://localhost:8080/user/search/findByNameAndSurname?name=gg%20g&surname=tt%20f
* http://localhost:8080/loan
* http://localhost:8080/loan/search/findByUserByUserId_UniqueId?uuid=7ba086c1-29af-4265-8f7d-27315e0c30b2
* add loan
```shell script
curl --location --request PUT 'http://localhost:8080/loan/add' \
--header 'Authorization: Basic YXBpVXNlcjpxd2VydHk=' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=938E2257A1921295E7A8FB8DB87A7387' \
--data-raw '{
    "name":"client name",
    "surname":"client surname",
    "loanAmount":20.0,
    "termDays":50
}'
```
* http://localhost:8080/blacklist/add/f733ebea-02ac-49da-9f59-4af2115911af

# TODO
* tests
* docker-compose up // test

# emotions
* https://www.youtube.com/watch?v=l6adCa9_d8I
* https://www.youtube.com/watch?v=cEGcNACF4G8