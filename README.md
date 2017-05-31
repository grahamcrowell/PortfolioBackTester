## Purpose1: 

Build a versatile application to both back test and forward test stock market strategies. The application should be able to take in algorithms based on set parameters (the strategy) and provide measurable results. The main output should be able to compare different strategies.

**Phase:** 1: Data Layer and Test Strategy

- Documentation of method and logic, and build shell
- Initial build of data layer
  - Create tables 
  - Download and parse data from Yahoo! Finance
  - Create test cases
- Build application to output results of one single strategy
  - Strategy: Cross between two simple moving average
  - Should be able to do this for any stock in the tables
  - Do not build portfolio

**Phase 2:** Portfolio & Strategy Integration (might split into two phases)

- Build Portfolio
  - See Portfolio Specifics below
- Build object to compare multiple strategies
  - Develop multiple strategies (EMA, Bollinger Bands) to test output of different/multiple strategies in portfolio
- Ensure flexibility (closed to modification but open to extension)
  - Strategy can be changed without affecting inner workings. Will only need to build strategy and input into the portfolio application, does not affect the data components or portofolia application. 
- Database refinement if needed

**Phase 3:** More Data & Replicating Models

- Decide if we want fundamental data at this point
- Research into trading models and replicate
- Obtain more data and create calculated fields – based on above points
  - Volatility measures
  - More granular timepoints
  - Fundamentals
  - Different markets (commodities, futures, currency)
  - Group stocks into industries, market cap, etc……
- Implement Monte Carlo model for forward testing

**Phase 4:** Further Analytics & Research

- Build custom models
  - Tool should be robust enough for us to implement various models

**Phase 5: **Presentation Layer

- Create presentation layer
- MORE TO COME!

## Portfolio Specifics

**Application Layer**

- Backtest(main) – input start date, end date, crystal balls and output measures comparing the portfolios (Phase 2)
  - Crystal Ball – input trade signals (Class TradeSignal) and executes trades for each day (Class Transaction) and sum into the portfolio (Class Portfolio). Output measures of one single strategy.
    - Plumbing – list of components required for Portfolio and Crystal ball methods?? (Phase 1)
  - Class stock – get stock and price for given day. Retrieve from database
      - def getPrice – get price on single day based on symbol
      - def getPrices – get price on multiple day (a list)
      - def getPriceChangePercent – get change in percentage based on two timepoints
  - Object market – basically same as stock but get a “market”/group of stocks
      - def exist??????
      - Calls Class stock
  - Class TradeSignal – gives recommendation to buy or sell a stock on one given day
      - Requires: stock, date, units, price
      - This will be modifiable?
  - Class Transaction – the actual function to buy or sell a stock in the porfolio
      - Looks at portfolio and divides units equally to divide up on stocks at a given date (if else based on TradeSignal)
      - This will be modifiable?
  - Class Position – history of transactions from crystal ball. Is it for investigative purposes?
  - Class Portfolio – stores results from CrystalBall
      - def executeTrate, getPosition, getPositions, getAvailableCash, getMarketValue

**Data Layer**
- AWS – database (Phase 1)
  - PostgreSQL for storage of raw data (archive and staging)
  - Slick library
  - Actors
- Implement Scalatest
