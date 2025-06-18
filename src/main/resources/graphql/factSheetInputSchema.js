import { z } from 'zod';

export const factSheetInputSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  type: z.string().min(1, 'Type is required'),
  lxExcludeFromQuota: z.boolean().optional().default(false),
  permittedReadACL: z.array(
    z.object({
      id: z.string().uuid().optional(),
      name: z.string().optional()
    })
  ).optional(),
  permittedWriteACL: z.array(
    z.object({
      id: z.string().uuid().optional(),
      name: z.string().optional()
    })
  ).optional()
}); 