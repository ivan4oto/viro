import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { Observable } from 'rxjs/internal/Observable';
import {environment} from "../../environments/environments";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loginStatus = new BehaviorSubject<boolean>(this.loggedIn())
  private username = new BehaviorSubject<string>(localStorage.getItem('username')!)
  private path = environment.apiUrl

  constructor(private httpClient: HttpClient) { }

  public signOutExternal = () => {
    localStorage.removeItem("token");
    console.log("token deleted")
  }

  LoginWithGoogle(credentials: string): Observable<any> {
    // const header = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpClient.post(this.path + "api/auth/google", {
      idTokenString: credentials
    })
  }

  LoginWithFacebook(credentials: string): Observable<any> {
    const header = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpClient.post(this.path + "LoginWithFacebook", JSON.stringify(credentials), { headers: header, withCredentials: true });
  }

  login(loginModel:any): Observable<any> {
    const header = new HttpHeaders().set('Content-type', 'application/json');

    return this.httpClient.post(this.path + 'Login', JSON.stringify(loginModel), { headers: header, withCredentials: true })

  }

  getClient(): Observable<any> {
    const header = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpClient.get(this.path + "GetColorList", { headers: header, withCredentials: true });
  }


  refreshToken(): Observable<any> {
    const header = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpClient.get(this.path + "RefreshToken", { headers: header, withCredentials: true });
  }

  revokeToken(): Observable<any> {
    const header = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpClient.delete(this.path + "RevokeToken/" + this.username.value, { headers: header, withCredentials: true });
  }

  saveToken(token:string) {
    localStorage.setItem('token', token)
  }

  saveUsername(username:string) {
    localStorage.setItem('username', username)
  }

  loggedIn(): boolean {
    if (localStorage.getItem('token')) {
      return true;
    }
    return false;
  }

  setLoginStatus(val:any) {
    this.loginStatus.next(val)
  }

  setUsername(val:any) {
    this.username.next(val)
  }


}
