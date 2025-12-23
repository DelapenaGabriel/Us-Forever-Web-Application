import axios from "axios";

export default{
    getTimeline(){
        return axios.get("/api/timeline");
    },

    getTimelineById(id){
        return axios.get(`/api/timeline/${id}`);
    },
    createTimeline(timeline){
        return axios.post("/api/timeline", timeline);
    },
    updateTimeline(id, timeline){
        return axios.put(`/api/timeline/${id}`, timeline);
    },
    deleteTimeline(id){
        return axios.delete(`/api/timeline/${id}`);
    }
}