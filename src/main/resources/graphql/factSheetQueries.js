export const SEARCH_FACT_SHEET_BY_NAME = `
  query searchFactSheetByName($name: String!) {
    allFactSheets(filter: {
      fullTextSearch: $name
    }) {
      edges {
        node {
          id
          name
          displayName
          fullName
          type
          description
          status
          lxState
          completion {
            completion
            percentage
          }
          updatedAt
          createdAt
          tags {
            name
          }
          ... on Application {
            lifecycle {
              asString
            }
            businessCriticality
            technicalSuitability
            functionalSuitability
          }
        }
      }
    }
  }
`; 