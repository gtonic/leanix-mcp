import { z } from 'zod';

export const factSheetSchema = z.object({
  id: z.string(),
  type: z.string(),
  name: z.string(),
  displayName: z.string().optional(),
  description: z.string().optional(),
  updatedAt: z.string()
}); 