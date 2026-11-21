const API='/api';
function token(){return localStorage.getItem('sb_token')}
function authHeaders(){return {'Content-Type':'application/json','Authorization':'Bearer '+token()}}
async function api(path,options={}) {
 const res=await fetch(API+path,{...options,headers:{'Content-Type':'application/json',...(token()?{'Authorization':'Bearer '+token()}:{}),...(options.headers||{})}});
 const data=await res.json().catch(()=>({message:'Unexpected server response'}));
 if(!res.ok) throw new Error(data.message||'Request failed'); return data;
}
function showError(id,msg){const e=document.getElementById(id);if(e)e.textContent=msg}
function money(v){return '₹'+Number(v).toLocaleString('en-IN',{minimumFractionDigits:2,maximumFractionDigits:2})}
