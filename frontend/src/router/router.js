import {createRouter, createWebHistory} from "vue-router";
import Home from "../views/Home.vue";
import Profile from "../views/Profile.vue";
import Login from "../views/Login.vue";
import {isLoggedIn, hasRole} from "../utils/auth";
import Register from "../views/Register.vue";
import CreatePost from "../views/CreatePost.vue";

const routes = [
    {
        path: "/",
        name: "home",
        component: Home
    },

    {
        path: "/login",
        name: "login",
        component: Login
    },

    {
        path: "/register",
        name: "register",
        component: Register
    },

    {
        path: "/profile",
        name: "profile",
        component: Profile,
        meta: {requiresAuth: true}
    },

    {
        path: "/post",
        name: "post",
        component: CreatePost,
        meta: {requiresAuth: true}
    }

    // {
    //     path: "/admin",
    //     name: "admin",
    //     component:
    // }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to) => {
    if (to.meta.requiresAuth) {
        const logged = await isLoggedIn();
        if (!logged) return "/login";
    }

    if (to.meta.roles) {
        const allowed = await hasRole(to.meta.roles);
        if (!allowed) return "/";
    }
});

export default router;