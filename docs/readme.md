**Data model **

Ticker

* Is a string 

Asset

* Has a

* Ticker 

* Return 

Stock

* Is a

    * Asset

* Has a 

    * Price time series 

    * Statement 

    * Exchange 

Time series 

* Is a

    * Map

* Has many 

    * (date, value) pairs 

Exchange 

* Is one of 

    * NYSE 

    * NASDAQ 

    * TORONTO 

    * AMEX

* Has a 

    * Currency 

Cash 

* Is a

    * Asset 

* Has a

    * Currency 

    * Quantity 

Currency 

* Is one of 

    * CAD 

    * USD

ETF

* Is a

    * Asset 

* Has a

    * Index 

Index 

* Is a

    * Portfolio 

Data domain 

* Is one of

    * Price

    * Financial statement 

Data source url 

* Has a

    * Ticker 

    * Date range 

    * Data domain 

    * String value

Return 

* Has a

* Date range 

* Rate of return 

* Book value logic 

Book value logic 

* Is a or

    * First in first out

    * First in last out

Trade order

* Has a

    * Asset 

    * Trade order type 

    * Order date time 

    * Quantity 

    * Unit price 

Trade execution 

* Has a

    * Trade order

    * Execution date time 

    * Fees

    * Total cost 

Trade history 

* Has many 

    * Trade execution 

Portfolio 

* Has a

    * Start date 

* Has many 

    * Holding

Holding

* Is a

    * Asset

* Has a

    * Trade history 

**Functions **

Book value   

* Input

    * Holding

    * Date 

* Output 

    * Value

Market value 

* Input

    * Holding

    * Date 

* Output 

    * Value

SMA 

EMA 

Metric 

Time series 

SMA 

Financial statement

* Is one of 

    * Balance

    * Income 

    * Cash flow 

* Has many 

    * Account

Account 

* Has many 

    * Sub account 

Fiscal quarter 

* Has a

    * Date range 

    * Number 

Date range

* Has a 

    * Start date 

    * End date 

