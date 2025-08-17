import * as Yup from "yup";

export const transactionSchema = Yup.object({
    amount: Yup.number().moreThan(0, "Amount must be positive"),
    description: Yup.string()
        .required("Description is required"),
});
