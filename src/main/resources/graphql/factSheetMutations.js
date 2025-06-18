export const CREATE_FACT_SHEET = `
  mutation createFactSheet($input: BaseFactSheetInput!) {
    createFactSheet(input: $input) {
      factSheet {
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
      }
    }
  }
`;

export const UPDATE_FACT_SHEET = `
  mutation updateFactSheet($id: ID!, $patches: [Patch!]!) {
    updateFactSheet(
      id: $id
      patches: $patches
    ) {
      factSheet {
        id
        name
        displayName
        type
        description
        status
        lxState
        completion {
          completion
          percentage
        }
        updatedAt
      }
    }
  }
`; 