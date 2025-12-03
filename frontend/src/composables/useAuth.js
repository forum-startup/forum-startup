import api from "../utils/axios.js";
import {currentUser} from "../utils/store.js";
import router from "../router/router.js";
import {ref} from "vue";

export function useAuth() {
    const form = ref({
        username: '',
        password: ''
    });

    const error = ref("");

    async function login() {
        try {
            error.value = "";
            await api.post("public/auth/login", form.value);

            currentUser.value = await fetchCurrentUser();

            await router.push("/");
        } catch (e) {
            const details = e?.response?.data?.details;
            error.value = details === "User is blocked"
                ? "Your account has been blocked due to violation of our terms of use."
                : "Invalid credentials.";
        }
    }

    async function fetchCurrentUser() {
        try {
            const res = await api.get("/private/auth/me");
            return res.data;
        } catch (err) {
            return null;
        }
    }

    function isLoggedIn() {
        return !!currentUser.value;
    }

    function hasRole(requiredRoles) {
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
            console.warn('Logout endpoint failed, clearing client state anyway', err);
        } finally {
            currentUser.value = null;
            await router.push("/");
            router.go(0);
        }
    }

    return {
        error,
        form,
        login,
        logout,
        fetchCurrentUser,
        isLoggedIn,
        hasRole,
    };
}