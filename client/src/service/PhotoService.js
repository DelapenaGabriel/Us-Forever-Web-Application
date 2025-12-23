import axios from "axios";

export default {
    getAllPhotos() {
        return axios.get("/api/photos");
    },
    getPhotosByCategory(category) {
        return axios.get(`/api/photos/category/${category}`);
    },
    getPhotoById(id) {
        return axios.get(`/api/photos/${id}`);
    },
    createPhoto(photo) {
        return axios.post("/api/photos", photo);
    },
    deletePhoto(id) {
        return axios.delete(`/api/photos/${id}`);
    },

}