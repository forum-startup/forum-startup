import {createRouter, createWebHistory} from "vue-router";
import Home from "../views/Home.vue";
import MyProfile from "../views/MyProfile.vue";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import SharePost from "../views/SharePost.vue";
import Users from "../views/Users.vue";
import MyPosts from "../views/MyPosts.vue";
import EditPost from "../views/EditPost.vue";
import Post from "../views/Post.vue";
import UserProfile from "../views/UserProfile.vue";
import {currentUser} from "../utils/store.js";

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
        component: MyProfile,
        meta: {requiresAuth: true}
    },

    {
        path: "/users",
        name: "Users",
        component: Users,
        meta: {requiresAuth: true}
    },

    {
        path: "/users/:userId",
        name: "UserProfile",
        component: UserProfile,
        meta: {requiresAuth: true}
    },

    {
        path: "/my-posts",
        name: "MyPosts",
        component: MyPosts,
        meta: {requiresAuth: true}
    },

    {
        path: "/share",
        name: "SharePost",
        component: SharePost,
        meta: {requiresAuth: true}
    },

    {
        path: "/my-posts/:postId/edit",
        name: "EditPost",
        component: EditPost,
        meta: {requiresAuth: true}
    },

    {
        path: "/posts/:postId",
        name: "Post",
        component: Post,
        meta: {requiresAuth: true}
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const isAuthenticated = !!currentUser.value;

    if (to.meta.requiresAuth && !isAuthenticated) {
        return next("/login");
    }

    if (to.meta.roles && isAuthenticated) {
        const userRoles = currentUser.value.roles?.map(r => r.name) || [];
        const hasRequiredRole = to.meta.roles.some(role => userRoles.includes(role));
        if (!hasRequiredRole) {
            return next("/");
        }
    }

    next();
});

export default router;