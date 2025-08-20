import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Welcome } from "./welcome";
import { useAuth } from "~/context/AuthContext";
import axios from "axios";
import { toast } from "react-toastify";
import '@testing-library/jest-dom';

jest.mock("axios");
jest.mock("~/context/AuthContext", () => ({
  useAuth: jest.fn(),
}));
jest.mock("react-toastify", () => ({
  toast: { success: jest.fn(), error: jest.fn() },
  ToastContainer: () => <div />,
  Bounce: jest.fn(),
}));


describe("Welcome Component", () => {
  const mockLogin = jest.fn();
  const user = userEvent.setup();

  beforeEach(() => {
    (useAuth as jest.Mock).mockReturnValue({ login: mockLogin });
    jest.clearAllMocks();
  });

  it("renders Sign In form by default", () => {
    render(<Welcome />);

    const form = screen.getByRole("form", { name: /sign in/i });
    expect(within(form).getByRole("button", { name: /^Sign In$/i })).toBeInTheDocument();
  });

  it("toggles to Create Account form", async () => {
    render(<Welcome />);

    // Click the "Create Account" tab/button
    await user.click(screen.getByRole("button", { name: /create account/i }));

    // Query the form by its actual aria-label
    const form = screen.getByRole("form", { name: /sign up form/i });

    // Assert that the "Create Account" submit button is present
    expect(
      within(form).getByRole("button", { name: /^Create Account$/i })
    ).toBeInTheDocument();
  });


  it("calls login on Sign In submit", async () => {
    render(<Welcome />);

    await user.type(screen.getByPlaceholderText(/johndoe@example.com/i), "johndoe@example.com");
    await user.type(screen.getByPlaceholderText("*********"), "password");
    const form = screen.getByRole("form", { name: /sign in/i });
    await user.click(within(form).getByRole("button", { name: /^Sign In$/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalled();
    });
  });

  it("handles successful registration", async () => {
    (axios.post as jest.Mock).mockResolvedValueOnce({ data: {} });

    render(<Welcome />);
    await user.click(screen.getByRole("button", { name: /create account/i }));

    await user.type(screen.getByPlaceholderText("John Doe"), "John Doe");
    await user.type(screen.getByPlaceholderText("johndoe@example.com"), "johndoe@example.com");
    const passwordFields = screen.getAllByPlaceholderText("*********");
    await user.type(passwordFields[0], "password123");
    await user.type(passwordFields[1], "password123");

    const form = screen.getByRole("form", { name: /sign up form/i });
    const submitButton = within(form).getByRole("button", { name: /^Create Account$/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining("auth/register"),
        expect.any(Object)
      );
      expect(toast.success).toHaveBeenCalledWith(
        expect.stringContaining("Registration successful"),
        expect.any(Object)
      );
    });
  });

  it("handles registration error with field error", async () => {
    (axios.post as jest.Mock).mockRejectedValueOnce({
      response: { data: { message: "Registration failed: Email already exists" } },
    });

    render(<Welcome />);
    await user.click(screen.getByRole("button", { name: /create account/i }));

    await user.type(screen.getByPlaceholderText("John Doe"), "John Doe");
    await user.type(screen.getByPlaceholderText("johndoe@example.com"), "johndoe@example.com");
    const passwordFields = screen.getAllByPlaceholderText("*********");
    await user.type(passwordFields[0], "password123");
    await user.type(passwordFields[1], "password123");

    const form = screen.getByRole("form", { name: /sign up form/i });
    const submitButton = within(form).getByRole("button", { name: /^Create Account$/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith(
        expect.stringContaining("Registration failed"),
        expect.any(Object)
      );
    });
  });


});
