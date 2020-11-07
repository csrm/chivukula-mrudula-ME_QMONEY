#!/bin/bash
date1=$(date | awk '{print $1}')
if [ $date1 == 'Saturday' -o $date1 == 'Sunday' ]
then
 if [ $date1 == 'Saturday' ]
 then
   date1=$(date --date="$date1 -1 day" +"%Y-%m-%d")
 elif [ $date1 == 'Sunday' ]
 then
   date1=$(date --date="$date1 -2 day" +"%Y-%m-%d")
 fi 
else
 date1=$(date +"%Y-%m-%d") 
fi
curl -X GET https://api.tiingo.com/tiingo/daily/GOOGL/prices\?startDate\=$date1\&endDate\=$date1\&token\=93c80accc7b3868d81c216fdbc53d889868fdd77
curl -X GET https://api.tiingo.com/tiingo/daily/AAPL/prices\?startDate\=$date1\&endDate\=$date1\&token\=93c80accc7b3868d81c216fdbc53d889868fdd77