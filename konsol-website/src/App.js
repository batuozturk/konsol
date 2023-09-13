import * as React from 'react';
import './App.css';
import PrivacyPolicy from './pages/PrivacyPolicy';
import Info from './pages/Features';
import Home from './pages/Home';
import TermsOfUse from './pages/TermsOfUse';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import MenuIcon from '@mui/icons-material/Menu';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import GithubIcon from '@mui/icons-material/GitHub';
import MailIcon from '@mui/icons-material/Mail';

const navItems = ['Features', 'Privacy Policy', 'Terms Of Use'];
const drawerWidth = 240;

function App(props) {
  const { window } = props;
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const navigate = useNavigate()
  const handleDrawerToggle = () => {
    setMobileOpen((prevState) => !prevState);
  };



  const handleRoute = (id) => {
    if (id === 'Features') navigate("/konsol/info")
    else if (id === 'Privacy Policy') navigate("/konsol/privacy-policy")
    else if (id === 'Terms Of Use') navigate("/konsol/terms-of-use")
    else navigate("/konsol")
  }

  const drawer = (
    <Box onClick={handleDrawerToggle} sx={{ textAlign: 'center' }}>
      <List>
        {navItems.map((item) => (
          <ListItem key={item} disablePadding>
            <ListItemButton sx={{ textAlign: 'center' }}>
              <ListItemText primary={item} />
            </ListItemButton>
          </ListItem>
        ))}
        <ListItem key={"View on Github"} disablePadding sx={{ textAlign: 'center', textDecoration: 'none' }}>
          <ListItemButton sx={{ textAlign: 'center', textDecoration: 'none' }} component="a" href="https://github.com/batuozturk/konsol" target="_blank">
            <GithubIcon sx={{ color: "#FD6C01" }} />
            <ListItemText primary="View on Github" sx={{ textAlign: 'center', textDecoration: 'none' }} />
          </ListItemButton>
        </ListItem>
        <ListItem key={"Send Email"} disablePadding sx={{ textAlign: 'center', textDecoration: 'none' }}>
          <ListItemButton sx={{ textAlign: 'center', textDecoration: 'none' }} component="a" href="mailto:batuoztrk99@gmail.com" target="_blank">
            <MailIcon sx={{ color: "#FD6C01" }} />
            <ListItemText primary="Send Email" sx={{ textAlign: 'center', textDecoration: 'none' }} />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );

  const container = window !== undefined ? () => window().document.body : undefined;

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar component="nav" style={{ 'background': "#FFFFFF" }} elevation={0}>
        <Toolbar>
          <IconButton
            color="#000000"
            aria-label="options"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography
            component="a"
            noWrap
            sx={{
              mr: 2,
              textDecoration: 'none',
              flexGrow: 1, display: { sm: 'block' },
              color: "#FD6C01", fontSize: 40, fontWeight: 500
            }}
            href="/konsol"
          >
            konsol
          </Typography>
          <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
            {navItems.map((item) => (
              <Button key={item} onClick={() =>
                handleRoute(item)
              } sx={{ color: '#fff' }} style={{ 'color': "#000000", 'fontSize': 20, 'fontWeight': 500, 'm': 0.5 }}>
                {item}
              </Button>
            ))}
            <Link to="https://github.com/batuozturk/konsol" target="_blank">
              <Button key={"View On Github"} onClick={() => {
              }} sx={{ color: '#fff' }} style={{ 'color': "#000000", 'fontSize': 20, 'fontWeight': 500 }}>
                <GithubIcon sx={{ color: "#FD6C01" }} />
                <Box sx={{ m: 0.5 }} />
                View On Github
              </Button>
            </Link>
            <Link to="mailto:batuoztrk99@gmail.com">
              <Button key={"Send Email"} onClick={() => {
              }} sx={{ color: '#fff' }} style={{ 'color': "#000000", 'fontSize': 20, 'fontWeight': 500 }}>
                <MailIcon sx={{ color: "#FD6C01" }} />
                <Box sx={{ m: 0.5 }} />
                Send Email
              </Button>
            </Link>
          </Box>
        </Toolbar>
      </AppBar>
      <nav>
        <Drawer
          container={container}
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, // Better open performance on mobile.
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
        >
          {drawer}
        </Drawer>
      </nav>
      <Box component="main" sx={{ p: 3 }}>
        <Toolbar />
        <Routes>
          <Route exact path='/konsol' element={< Home />}></Route>
          <Route exact path='/konsol/info' element={< Info />}></Route>
          <Route exact path='/konsol/privacy-policy' element={< PrivacyPolicy />}></Route>
          <Route exact path='/konsol/terms-of-use' element={< TermsOfUse />}></Route>
        </Routes>
      </Box>
    </Box>
  );
}

export default App;
