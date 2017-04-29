# Crystal Ball

## Purpose1: 

Build a versatile application to both back test and forward test stock market strategies. The application should be able to take in algorithms based on set parameters (the strategy) and provide measurable results. The main output string together and compare the results.

**Phase:** 1: Initial Creation

- Documentation of method and logic, and build shell
- Initial build of data layer
- Build application to output results of one single strategy

**Phase 2:** Refinement

- Determine outputs to compare measures
- Build object to compare multiple strategies
- Ensure flexibility (closed to modification but open to extension)
  - Strategy can be changed without affecting other components of application
- Database refinement

**Phase 3:** Analytics & Research – Part 1 Replication

- Research into a few trading models
- Obtain more data or and create calculated fields – based on above points
  - Volatility measures
  - Granular data
  - Different markets (commodities, futures, currency)
  - Group stocks into industries, market cap, etc……
- Implement Monte Carlo model for forward testing

**Phase 4:** Analytics & Research – Part 2 Custom Models & Presentation

- Tool should be robust enough for us to implement various models
- Create presentation layer

## Phase 1 Specifics

1. Documentation of method and logic

**Main Goal: In words, describe how the application and data layer will be built with a focus on Phase 1 and Phase 2**

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

To do: clarify all input and outputs

**Data Layer**
- AWS – database (Phase 1)
  - PostgreSQL for storage of raw data (archive and staging)
  - Spark SQL for interface to application layer
  - Abstracts away implementation of data as collection of scala collections
- Custom scala updater using Slick for batch updates (Phase 2?)


