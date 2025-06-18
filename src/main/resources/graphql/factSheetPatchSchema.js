import { z } from 'zod';

export const PatchOperation = {
  ADD: 'add',
  REPLACE: 'replace',
  REMOVE: 'remove'
};

export const factSheetPatchSchema = z.array(
  z.object({
    op: z.enum([PatchOperation.ADD, PatchOperation.REPLACE, PatchOperation.REMOVE]),
    path: z.string(),
    value: z.any().optional() // value is optional for REMOVE operations
  })
); 