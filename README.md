# Hotel Resource Management 

This Project receives hotel resources details like water ,electricity used and waste producer and provides report on resources .


## Getting Started

Check out and run "mvn clean install " make sure all test case passed .It uses embedded Kafka ,Zookeeper and Mongo .make sure default port of these are not used 

## Project Structure 

This is maven **multi-module** project .


**Entities:** Contains common classes
 
**Api:** Provides rest end points to get resource details and provide report 

**SP:** Kafka Stream Processor to process events from Kafka topic  


## End Points 

**To submit Resource details:**
           /v1.0/hotel/resource/{hotelId}
           
*sample payload*
```
{
	"hotelId": "hot123",
	"hotelName": "abc",
	"timeStamp": 1555784697059,
	"resources": [{
			"resourceType": "WATER",
			"waterConsumed": 1000
		},
		{
			"resourceType": "ELECTRICITY",
			"electricityConsumed": 1000
		},
		{
			"resourceType": "WASTE",
			"wasteProduced": 1000
		}
	]
}
```



**To get Report:**
/v1.0/hotel/report/{hotelId}??startTimeStamp={statTimestamp}&endTimeStamp={endTimestamp}

*sample response*
```
{
	"hotelId": "hot123",
	"hotelName": "abc",
	"startDate": 1555784697000,
	"endDate": 1555784698000,
	"resources": [{
		"resourceType": "WASTE",
		"units": "KG",
		"wasteProduced": 2000
	}, {
		"resourceType": "ELECTRICITY",
		"units": "KWH",
		"electricityConsumed": 2000
	}, {
		"resourceType": "WATER",
		"units": "Liters",
		"waterConsumed": 2000
	}]
}
```


