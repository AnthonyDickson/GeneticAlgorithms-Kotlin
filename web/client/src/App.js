import './App.css';

import React, {Component} from 'react';
import {Breadcrumb, Button, Form, Icon, Input, Layout, Menu, Typography} from 'antd';

class App extends Component {
    state = {
        response: '',
        post: '',
        responseToPost: '',
    };

    componentDidMount() {
        this.callApi()
            .then(res => this.setState({response: res.express}))
            .catch(err => console.log(err));
    }

    callApi = async () => {
        const response = await fetch('/api/hello');
        const body = await response.json();
        if (response.status !== 200) throw Error(body.message);

        return body;
    };

    handleSubmit = async e => {
        e.preventDefault();
        const response = await fetch('/api/world', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({post: this.state.post}),
        });
        const body = await response.text();

        this.setState({responseToPost: body});
    };

    render() {
        // TODO: Use React Router for breadcrumbs. See: https://ant.design/components/breadcrumb/
        const routes = [
            {
                path: 'index',
                breadcrumbName: 'First-level Menu',
            },
            {
                path: 'first',
                breadcrumbName: 'Second-level Menu',
            },
            {
                path: 'second',
                breadcrumbName: 'Third-level Menu',
            },
        ];

        return (
            <Layout>
                <Layout.Sider
                    style={{
                        overflow: 'auto',
                        height: '100vh',
                        position: 'fixed',
                        left: 0,
                    }}
                >
                    <div className="logo"/>
                    <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']}>
                        <Menu.Item key="1">
                            <Icon type="ordered-list"/>
                            <span className="nav-text">Runs</span>
                        </Menu.Item>
                        <Menu.Item key="2">
                            <Icon type="bar-chart"/>
                            <span className="nav-text">Default Dashboard</span>
                        </Menu.Item>
                    </Menu>
                </Layout.Sider>
                <Layout style={{marginLeft: 200}}>
                    <Layout.Header style={{background: '#fff', padding: '24px 16px', minHeight: 120}}>
                        <Typography>
                            <Typography.Title>Title</Typography.Title>
                            <Breadcrumb routes={routes}/>
                        </Typography>
                    </Layout.Header>
                    <Layout.Content style={{margin: '24px 16px 0'}}>
                        <div style={{padding: 24, background: '#fff', minHeight: 360}}>
                            <p>{this.state.response}</p>
                            <Form onSubmit={this.handleSubmit} className="server-message-form">
                                <Form.Item>
                                    <strong>Post to Server:</strong>
                                    <Input
                                        type="text"
                                        placeholder="Message"
                                        value={this.state.post}
                                        onChange={e => this.setState({post: e.target.value})}
                                    />
                                </Form.Item>

                                <Form.Item>
                                    <Button type="primary" htmlType="submit">Submit</Button>
                                </Form.Item>
                            </Form>
                            <p>{this.state.responseToPost}</p>
                        </div>
                    </Layout.Content>
                    <Layout.Footer style={{textAlign: 'center'}}>Ant Design ©2018 Created by Ant UED</Layout.Footer>
                </Layout>
            </Layout>
        );
    }
}

export default App;