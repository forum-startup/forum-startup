import api from "./axios.js";
import {currentUser} from "./store.js";
import router from "../router/router.js";
import {ref} from "vue";

export function useAuth() {
    const form = ref({
        username: '',
        password: ''
    })

    const error = ref("")

    async function login() {
        try {
            error.value = ""
            await api.post("public/auth/login", form.value)

            currentUser.value = await fetchCurrentUser()

            await router.push("/")
        } catch (e) {
            const details = e?.response?.data?.details;

            if (details === "User is blocked") {
                error.value = "Your account has been blocked most likely due to violation of our terms of use."
                return
            }
            error.value = "Invalid credentials."
        }
    }

// Fetch current logged-in user based on cookie
    async function fetchCurrentUser() {
        try {
            const res = await api.get("/private/auth/me");
            return res.data;
        } catch (err) {
            return null;
        }
    }

// Fetch user by id
    async function fetchUserById(id) {
        try {
            const res = await api.get("/private/auth/me");
            return res.data;
        } catch (err) {
            return null;
        }
    }

    async function isLoggedIn() {
        return (await fetchCurrentUser()) !== null;
    }

    async function hasRole(requiredRoles) {
        const user = await fetchCurrentUser();
        if (!user) return false;
        const userRoles = user.roles?.map(r => r.name) || [];
        return requiredRoles.some(role => userRoles.includes(role));
    }

// Client-side reactive check
    function hasRoleReactive(requiredRoles) {
        if (!currentUser.value) return false;
        const userRoles = currentUser.value.roles?.map(r => r.name) || [];
        return Array.isArray(requiredRoles)
            ? requiredRoles.some(role => userRoles.includes(role))
            : userRoles.includes(requiredRoles);
    }

    async function logout() {
        try {
            await api.post('/private/auth/logout');
        } catch (err) {
            console.warn('Logout endpoint failed, but clearing client state anyway', err);
        } finally {
            currentUser.value = null;
        }
    }

    return {
        error,
        form,
        login,
        fetchCurrentUser,
        fetchUserById,
        isLoggedIn,
        hasRoleReactive,
        logout,
    }
}