// Query for getting fact sheet counts and workspace overview
export const GET_FACT_SHEET_COUNTS = `
  query {
    allFactSheets {
      totalCount
      filterOptions {
        facets {
          facetKey
          results {
            name
            key
            count
          }
        }
      }
    }
  }
`;