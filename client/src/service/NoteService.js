import axios from "axios";;

export default {

    getAllNotes() {
        return axios.get("/api/notes");
    },

    getNoteById(id) {
        return axios.get(`/api/notes/${id}`);
    },

    createNote(note) {
        return axios.post("/api/notes", note);
    },

    updateNote(id, note) {
        return axios.put(`/api/notes/${id}`, note);
    },
    deleteNote(id) {
        return axios.delete(`/api/notes/${id}`);
    }

}