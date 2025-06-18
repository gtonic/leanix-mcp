export const GET_FACT_SHEET_SUBSCRIPTIONS = `
  query getFactSheetSubscriptions($factSheetId: ID!) {
    factSheet(id: $factSheetId) {
      id
      displayName
      fullName
      status
      lxState
      completion {
        completion
        percentage
      }
      subscriptions {
        edges {
          node {
            id
            type
            user {
              id
              displayName
              email
            }
            roles {
              id
              name
              comment
            }
            createdAt
          }
        }
        totalCount
      }
      updatedAt
      createdAt
    }
  }
`; 