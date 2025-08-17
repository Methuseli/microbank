
import * as Yup from "yup";

export const registrationSchema = Yup.object({
    name: Yup.string()
        .trim()
        .min(2, "Name is too short")
        .max(100, "Name is too long")
        .required("Full name is required"),
    email: Yup.string()
        .trim()
        .email("Enter a valid email")
        .required("Email is required"),
    password: Yup.string()
        .min(8, "Minimum 8 characters")
        .matches(/[A-Z]/, "At least one uppercase letter")
        .matches(/[a-z]/, "At least one lowercase letter")
        .matches(/\d/, "At least one number")
        .matches(/[^A-Za-z0-9]/, "At least one special character")
        .required("Password is required"),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref("password")], "Passwords must match")
        .required("Confirm your password"),
});
