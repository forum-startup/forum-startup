import api from "./axios.js";
import { currentUser } from "./store.js";

// Fetch current logged-in user based on cookie
export async function fetchCurrentUser() {
    try {
        const res = await api.get("/private/auth/me");
        return res.data;
    } catch (err) {
        return null;
    }
}

// Fetch user by id
export async function fetchUserById(id) {
    try {
        const res = await api.get("/private/auth/me");
        return res.data;
    } catch (err) {
        return null;
    }
}

export async function isLoggedIn() {
    return (await fetchCurrentUser()) !== null;
}

export async function hasRole(requiredRoles) {
    const user = await fetchCurrentUser();
    if (!user) return false;
    const userRoles = user.roles?.map(r => r.name) || [];
    return requiredRoles.some(role => userRoles.includes(role));
}

// 2. Client-side reactive check (perfect for UI: v-if, computed, etc.)
export function hasRoleReactive(requiredRoles) {
    if (!currentUser.value) return false;
    const userRoles = currentUser.value.roles?.map(r => r.name) || [];
    return Array.isArray(requiredRoles)
        ? requiredRoles.some(role => userRoles.includes(role))
        : userRoles.includes(requiredRoles);
}

export async function logout() {
    try {
        await api.post('/private/auth/logout');
    } catch (err) {
        console.warn('Logout endpoint failed, but clearing client state anyway', err);
    } finally {
        // always clears currentUser after a logout
        currentUser.value = null;
    }
}

