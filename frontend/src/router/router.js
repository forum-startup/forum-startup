import {createRouter, createWebHistory} from "vue-router";
import Home from "../views/Home.vue";
import Profile from "../views/Profile.vue";
import Login from "../views/Login.vue";
import {isLoggedIn, hasRole} from "../utils/auth";
import Register from "../views/Register.vue";
import SharePost from "../views/SharePost.vue";
import Users from "../views/Users.vue";
import MyPosts from "../views/MyPosts.vue";
import EditPost from "../views/EditPost.vue";

const routes = [
    {
        path: "/",
        name: "Home",
        component: Home
    },

    {
        path: "/login",
        name: "Login",
        component: Login
    },

    {
        path: "/register",
        name: "Register",
        component: Register
    },

    {
        path: "/profile",
        name: "Profile",
        component: Profile,
        meta: {requiresAuth: true}
    },

    {
        path: "/share",
        name: "SharePost",
        component: SharePost,
        meta: {requiresAuth: true}
    },

    {
        path: "/users",
        name: "Users",
        component: Users,
        meta: {requiresAuth: true}
    },

    {
        path: "/my-posts",
        name: "MyPosts",
        component: MyPosts,
        meta: {requiresAuth: true}
    },

    {
        path: "/my-posts/:postId/edit",
        name: "EditPost",
        component: EditPost,
        meta: {requiresAuth: true}
    },
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