var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        accountToNumber: "VIN",
        investmentId: 0,
        errorToats: null,
        errorMsg: null,
    },
    methods:{
        getData: function(){
            axios.get("/api/clients/current")
                .then((response) => {
                    //get client ifo
                    this.clientInfo = response.data;
                })
                .catch((error)=>{
                    // handle error
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function(){
            axios.post('/api/logout')
                .then(response => window.location.href="/web/index.html")
                .catch(() =>{
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        setIdInvestment: function(id){
            if(this.accountToNumber == "VIN"){
                 this.errorMsg = "Debe seleccionar una cuenta de Retiro";
                 this.errorToats.show();
            } else{
                this.investmentId = id;
                let config = {
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded'
                    }
                }
                axios.post(`/api/investments?id=${this.investmentId}&toAccountNumber=${this.accountToNumber}`,config)
                 .then(response => window.location.reload())
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
            }
        },
        create: function(){
            axios.post('/api/clients/current/accounts')
                .then(response => window.location.reload())
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
        }
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
    }
})